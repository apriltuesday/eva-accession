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
package uk.ac.ebi.eva.accession.clustering.configuration.batch.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.ac.ebi.eva.accession.clustering.configuration.InputParametersConfiguration;
import uk.ac.ebi.eva.accession.clustering.parameters.InputParameters;
import uk.ac.ebi.eva.commons.batch.io.AggregatedVcfReader;
import uk.ac.ebi.eva.commons.batch.io.VcfReader;
import uk.ac.ebi.eva.commons.core.models.Aggregation;

import java.io.File;
import java.io.IOException;

import static uk.ac.ebi.eva.accession.clustering.configuration.BeanNames.CLUSTERING_READER;

@Configuration
@Import(InputParametersConfiguration.class)
public class VcfReaderConfiguration {

    @Autowired
    private InputParameters inputParameters;

    @Bean(CLUSTERING_READER)
    public VcfReader vcfReader() throws IOException {
//        String fileId = inputParameters.getProjectAccession();
//        String studyId = inputParameters.getProjectAccession();
        String fileId = "file";
        String studyId = "study";
        File vcfFile = new File(inputParameters.getVcf());
        return new AggregatedVcfReader(fileId, studyId, Aggregation.BASIC, null, vcfFile);
    }
}
