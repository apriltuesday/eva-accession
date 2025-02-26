/*
 * Copyright 2022 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.eva.accession.clustering.configuration.batch.io;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import uk.ac.ebi.eva.accession.clustering.batch.io.ClusteringWriter;
import uk.ac.ebi.eva.accession.clustering.batch.io.SSSplitWriter;
import uk.ac.ebi.eva.accession.clustering.parameters.InputParameters;
import uk.ac.ebi.eva.accession.core.configuration.nonhuman.ClusteredVariantAccessioningConfiguration;
import uk.ac.ebi.eva.accession.core.configuration.nonhuman.MongoConfiguration;
import uk.ac.ebi.eva.accession.core.configuration.nonhuman.SubmittedVariantAccessioningConfiguration;
import uk.ac.ebi.eva.accession.core.model.eva.SubmittedVariantEntity;
import uk.ac.ebi.eva.accession.core.service.nonhuman.SubmittedVariantAccessioningService;
import uk.ac.ebi.eva.metrics.metric.MetricCompute;

import static uk.ac.ebi.eva.accession.clustering.configuration.BeanNames.CLUSTERED_CLUSTERING_WRITER;
import static uk.ac.ebi.eva.accession.clustering.configuration.BeanNames.SS_SPLIT_WRITER;

@Configuration
@Import({ClusteredVariantAccessioningConfiguration.class, SubmittedVariantAccessioningConfiguration.class,
        ClusteringWriterConfiguration.class, MongoConfiguration.class})
public class SSSplitWriterConfiguration {
    @Bean(SS_SPLIT_WRITER)
    public ItemWriter<SubmittedVariantEntity> ssSplitWriter(
            InputParameters inputParameters,
            @Qualifier(CLUSTERED_CLUSTERING_WRITER) ClusteringWriter clusteringWriter,
            SubmittedVariantAccessioningService submittedVariantAccessioningService,
            MongoTemplate mongoTemplate, MetricCompute metricCompute) {
        return new SSSplitWriter(inputParameters.getAssemblyAccession(), clusteringWriter,
                                 submittedVariantAccessioningService, mongoTemplate, metricCompute);
    }
}
