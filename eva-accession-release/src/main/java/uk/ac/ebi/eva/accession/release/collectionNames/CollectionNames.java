/*
 * Copyright 2020 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.eva.accession.release.collectionNames;

/**
 * As we have to do the same operations for EVA and dbSNP releases, where only the table names change, this class
 * allows using polymorphism to only change the CollectionNames implementation, and not change the release operations.
 */
public interface CollectionNames {

    String getClusteredVariantEntity();

    String getSubmittedVariantEntity();

    String getClusteredVariantOperationEntity();

    String getSubmittedVariantOperationEntity();
}
