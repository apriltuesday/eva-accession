/*
 * Copyright 2021 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.eva.accession.clustering.batch.io;

import com.mongodb.MongoBulkWriteException;

import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionCouldNotBeGeneratedException;
import uk.ac.ebi.ampt2d.commons.accession.core.models.EventType;
import uk.ac.ebi.ampt2d.commons.accession.generators.monotonic.MonotonicAccessionGenerator;
import uk.ac.ebi.ampt2d.commons.accession.persistence.mongodb.document.EventDocument;

import uk.ac.ebi.eva.accession.clustering.batch.io.ClusteredVariantSplittingPolicy.SplitDeterminants;
import uk.ac.ebi.eva.accession.core.model.IClusteredVariant;
import uk.ac.ebi.eva.accession.core.model.ISubmittedVariant;
import uk.ac.ebi.eva.accession.core.model.dbsnp.DbsnpSubmittedVariantEntity;
import uk.ac.ebi.eva.accession.core.model.dbsnp.DbsnpSubmittedVariantOperationEntity;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantEntity;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantInactiveEntity;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantOperationEntity;
import uk.ac.ebi.eva.accession.core.model.eva.SubmittedVariantEntity;
import uk.ac.ebi.eva.accession.core.model.eva.SubmittedVariantInactiveEntity;
import uk.ac.ebi.eva.accession.core.model.eva.SubmittedVariantOperationEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

public class RSSplitWriter implements ItemWriter<SubmittedVariantOperationEntity> {

    private final ClusteringWriter clusteringWriter;

    private final MonotonicAccessionGenerator<IClusteredVariant> clusteredVariantAccessionGenerator;

    private static final String ACCESSION_ATTRIBUTE = "accession";

    private static final String EVENT_TYPE_ATTRIBUTE = "eventType";

    private static final String SPLIT_INTO_ATTRIBUTE = "splitInto";

    private static final String ASSEMBLY_ATTRIBUTE_IN_CLUSTERED_OPERATIONS_COLLECTION = "inactiveObjects.asm";

    private final MongoTemplate mongoTemplate;

    public RSSplitWriter(ClusteringWriter clusteringWriter,
                         MonotonicAccessionGenerator<IClusteredVariant> clusteredVariantAccessionGenerator,
                         MongoTemplate mongoTemplate) {
        this.clusteringWriter = clusteringWriter;
        this.clusteredVariantAccessionGenerator = clusteredVariantAccessionGenerator;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void write(@Nonnull List<? extends SubmittedVariantOperationEntity> submittedVariantOperationEntities)
            throws MongoBulkWriteException, AccessionCouldNotBeGeneratedException {
        for (SubmittedVariantOperationEntity entity: submittedVariantOperationEntities) {
            writeRSSplit(entity);
            this.mongoTemplate.remove(entity,
                                      this.mongoTemplate.getCollectionName(SubmittedVariantOperationEntity.class));
        }
    }

    public void writeRSSplit(SubmittedVariantOperationEntity submittedVariantOperationEntity)
            throws AccessionCouldNotBeGeneratedException {
        // For each sets of RS hashes among the split candidates
        // get a triple with
        // 1) ClusteredVariant object with the hash 2) the hash itself and 3) number of variants with that hash
        // ex: if the split candidates post-remapping are:
        // SS1, RS1, LOC1, SNV, C/A
        // SS2, RS1, LOC2, SNV, C/T
        // SS3, RS1, LOC2, SNV, T/G
        // SS4, RS1, LOC3, SNV, T/A
        // the result will be a list of 3 triples:
        // RS1, hash(LOC1), 1
        // RS1, hash(LOC2), 2
        // RS1, hash(LOC3), 1
        List<SplitDeterminants> splitCandidates =
                submittedVariantOperationEntity
                        .getInactiveObjects()
                        .stream()
                        .map(SubmittedVariantInactiveEntity::toSubmittedVariantEntity)
                        .collect(Collectors.groupingBy(this::getRSHashForSS))
                        .entrySet().stream().map(rsHashAndAssociatedSS ->
                                new SplitDeterminants(
                                        clusteringWriter.toClusteredVariantEntity(rsHashAndAssociatedSS.getValue()
                                                                                                       .get(0)),
                                        rsHashAndAssociatedSS.getKey(),
                                        rsHashAndAssociatedSS.getValue().size(),
                                        // Get lowest SS ID associated with a given RS hash
                                        rsHashAndAssociatedSS.getValue().stream()
                                                             .map(SubmittedVariantEntity::getAccession)
                                                             .min(Comparator.naturalOrder()).get()))
                        .collect(Collectors.toList());
        // Based on the split policy, one of the hashes will retain the RS associated with it
        // and the other hashes should be associated with new RS IDs
        List<String> hashesThatShouldGetNewRS =  getHashesThatShouldGetNewRS(splitCandidates);
        issueNewRSForHashes(hashesThatShouldGetNewRS, submittedVariantOperationEntity.getInactiveObjects());
    }



    private void issueNewRSForHashes(List<String> hashesThatShouldGetNewRS,
                                     List<SubmittedVariantInactiveEntity> submittedVariantInactiveEntities)
            throws AccessionCouldNotBeGeneratedException {
        Map<String, List<SubmittedVariantEntity>> rsHashAndAssociatedSS =
                submittedVariantInactiveEntities
                .stream()
                .map(SubmittedVariantInactiveEntity::toSubmittedVariantEntity)
                .collect(Collectors.groupingBy(this::getRSHashForSS));
        for (String rsHash: rsHashAndAssociatedSS.keySet()) {
            if (hashesThatShouldGetNewRS.contains(rsHash)) {
                Long newRSAccession =
                        this.clusteredVariantAccessionGenerator.generateAccessions(1)[0];
                List<SubmittedVariantEntity> associatedSSEntries = rsHashAndAssociatedSS.get(rsHash);
                for (SubmittedVariantEntity submittedVariantEntity: associatedSSEntries) {
                    Long oldRSAccession =  submittedVariantEntity.getClusteredVariantAccession();
                    associateNewRSToSS(newRSAccession, submittedVariantEntity);
                    writeRSUpdateOperation(oldRSAccession, newRSAccession,
                                           clusteringWriter.toClusteredVariantEntity(submittedVariantEntity));
                    writeSSUpdateOperation(oldRSAccession, newRSAccession, submittedVariantEntity);
                }
            }
        }
    }

    private String getRSHashForSS(SubmittedVariantEntity submittedVariantEntity) {
        return clusteringWriter.toClusteredVariantEntity(submittedVariantEntity).getHashedMessage();
    }

    private void associateNewRSToSS(Long newRSAccession,
                                    SubmittedVariantEntity submittedVariantEntity) {
        final String accessionAttribute = "accession";
        final String assemblyAttribute = "seq";
        final String clusteredVariantAttribute = "rs";

        Class<? extends SubmittedVariantEntity> submittedVariantClass =
                clusteringWriter.isEvaSubmittedVariant(submittedVariantEntity) ?
                        SubmittedVariantEntity.class : DbsnpSubmittedVariantEntity.class;
        Query queryToFindSS = query(where(assemblyAttribute).is(submittedVariantEntity.getReferenceSequenceAccession()))
                .addCriteria(where(accessionAttribute).is(submittedVariantEntity.getAccession()));
        Update updateRS = update(clusteredVariantAttribute, newRSAccession);
        this.mongoTemplate.updateMulti(queryToFindSS, updateRS, submittedVariantClass);
    }

    private void writeRSUpdateOperation(Long oldRSAccession, Long newRSAccession,
                                        ClusteredVariantEntity clusteredVariantEntity) {
        // Choose which operation collection to write to: EVA or dbSNP
        Class<? extends EventDocument<IClusteredVariant, Long, ? extends ClusteredVariantInactiveEntity>>
                operationClass = clusteringWriter.getClusteredOperationCollection(oldRSAccession);
        ClusteredVariantOperationEntity splitOperation = new ClusteredVariantOperationEntity();
        String splitOperationDescription = "Due to hash mismatch, rs" + newRSAccession +
                " was issued to split from rs" + oldRSAccession + ".";
        splitOperation.fill(EventType.RS_SPLIT, oldRSAccession, newRSAccession, splitOperationDescription,
                            Collections.singletonList(new ClusteredVariantInactiveEntity(clusteredVariantEntity)));
        Query queryToCheckPreviousRSOperation = query(where(ACCESSION_ATTRIBUTE).is(oldRSAccession))
                .addCriteria(where(EVENT_TYPE_ATTRIBUTE).is(EventType.RS_SPLIT))
                .addCriteria(where(SPLIT_INTO_ATTRIBUTE).is(newRSAccession))
                .addCriteria(where(ASSEMBLY_ATTRIBUTE_IN_CLUSTERED_OPERATIONS_COLLECTION)
                                     .is(clusteredVariantEntity.getAssemblyAccession()));
        if (this.mongoTemplate.find(queryToCheckPreviousRSOperation, operationClass).isEmpty()) {
            this.mongoTemplate.insert(splitOperation, this.mongoTemplate.getCollectionName(operationClass));
        }
    }

    private void writeSSUpdateOperation(Long oldRSAccession, Long newRSAccession,
                                        SubmittedVariantEntity submittedVariantEntity) {
        // Choose which operation collection to write to: EVA or dbSNP
        Class<? extends EventDocument<ISubmittedVariant, Long, ? extends SubmittedVariantInactiveEntity>>
                operationClass = clusteringWriter.isEvaSubmittedVariant(submittedVariantEntity) ?
                SubmittedVariantOperationEntity.class : DbsnpSubmittedVariantOperationEntity.class;
        SubmittedVariantOperationEntity updateOperation = new SubmittedVariantOperationEntity();
        String updateOperationDescription = "SS was associated with the split RS rs" + newRSAccession
                + " that was split from rs" + oldRSAccession + " after remapping.";
        updateOperation.fill(EventType.UPDATED, submittedVariantEntity.getAccession(), updateOperationDescription,
                             Collections.singletonList(new SubmittedVariantInactiveEntity(submittedVariantEntity)));
        this.mongoTemplate.insert(updateOperation, this.mongoTemplate.getCollectionName(operationClass));
    }

    /**
     * Get hashes that should receive the new RS
     * @param splitCandidates List of triples
     *                        1) ClusteredVariant object with the hash 2) the hash itself and
     *                        3) the number of variants with that hash
     * @return Hashes that should be associated with a new RS
     */
    private List<String> getHashesThatShouldGetNewRS(List<SplitDeterminants>
                                                             splitCandidates) {
        SplitDeterminants lastPrioritizedHash = splitCandidates.get(0);
        for (int i = 1; i < splitCandidates.size(); i++) {
            lastPrioritizedHash = ClusteredVariantSplittingPolicy.prioritise(
                    lastPrioritizedHash, splitCandidates.get(i)).hashThatShouldRetainOldRS;
        }
        final SplitDeterminants hashThatShouldRetainOldRS = lastPrioritizedHash;
        this.mongoTemplate.save(hashThatShouldRetainOldRS.getClusteredVariantEntity(),
                                this.mongoTemplate.getCollectionName(clusteringWriter.getClusteredVariantCollection(
                                        hashThatShouldRetainOldRS.getClusteredVariantEntity().getAccession()))
        );
        return splitCandidates.stream()
                              .map(SplitDeterminants::getRsHash)
                              .filter(rsHash -> !(rsHash.equals(hashThatShouldRetainOldRS.getRsHash())))
                              .collect(Collectors.toList());
    }
}
