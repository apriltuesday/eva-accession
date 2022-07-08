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
package uk.ac.ebi.eva.accession.deprecate.test.configuration;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import uk.ac.ebi.eva.accession.core.configuration.nonhuman.MongoConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.InputParametersConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.policies.ChunkSizeCompletionPolicyConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.io.StudySubmittedVariantsReaderConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.jobs.DeprecateStudySubmittedVariantsJobConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.steps.DeprecateStudySubmittedVariantsStepConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.io.SubmittedVariantDeprecationWriterConfiguration;
import uk.ac.ebi.eva.accession.deprecate.configuration.batch.listeners.ListenerConfiguration;

@EnableAutoConfiguration
@Import({MongoConfiguration.class,
        InputParametersConfiguration.class,
        ChunkSizeCompletionPolicyConfiguration.class,
        StudySubmittedVariantsReaderConfiguration.class,
        SubmittedVariantDeprecationWriterConfiguration.class,
        DeprecateStudySubmittedVariantsStepConfiguration.class,
        DeprecateStudySubmittedVariantsJobConfiguration.class,
        ListenerConfiguration.class
})
public class BatchTestConfiguration {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}
