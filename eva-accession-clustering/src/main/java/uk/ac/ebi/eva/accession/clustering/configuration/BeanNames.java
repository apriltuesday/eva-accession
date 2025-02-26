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
package uk.ac.ebi.eva.accession.clustering.configuration;

public class BeanNames {

    public static final String VCF_READER = "VCF_READER";

    public static final String CLUSTERED_VARIANTS_MONGO_READER = "CLUSTERED_VARIANTS_MONGO_READER";

    public static final String NON_CLUSTERED_VARIANTS_MONGO_READER = "NON_CLUSTERED_VARIANTS_MONGO_READER";

    public static final String STUDY_CLUSTERING_MONGO_READER = "STUDY_CLUSTERING_MONGO_READER";

    public static final String RS_MERGE_CANDIDATES_READER = "RS_MERGE_CANDIDATES_READER";

    public static final String RS_SPLIT_CANDIDATES_READER = "RS_SPLIT_CANDIDATES_READER";

    public static final String CLEAR_RS_MERGE_AND_SPLIT_CANDIDATES = "CLEAR_RS_MERGE_AND_SPLIT_CANDIDATES";

    public static final String RS_MERGE_WRITER = "RS_MERGE_WRITER";

    public static final String RS_REPORT_FILE = "RS_REPORT_FILE";

    public static final String RS_SPLIT_WRITER = "RS_SPLIT_WRITER";

    public static final String SS_SPLIT_WRITER = "SS_SPLIT_WRITER";

    public static final String TARGET_SS_READER_FOR_NEW_BACKPROP_RS = "TARGET_SS_READER_FOR_NEW_BACKPROP_RS";

    public static final String TARGET_SS_READER_FOR_SPLIT_OR_MERGED_BACKPROP_RS
            = "TARGET_SS_READER_FOR_SPLIT_OR_MERGED_BACKPROP_RS";

    public static final String BACK_PROPAGATED_RS_WRITER = "BACK_PROPAGATED_RS_WRITER";

    public static final String VARIANT_TO_SUBMITTED_VARIANT_ENTITY_PROCESSOR =
            "VARIANT_TO_SUBMITTED_VARIANT_ENTITY_PROCESSOR";

    public static final String NON_CLUSTERED_CLUSTERING_WRITER = "NON_CLUSTERED_CLUSTERING_WRITER";

    public static final String CLUSTERED_CLUSTERING_WRITER = "CLUSTERED_CLUSTERING_WRITER";

    public static final String PROGRESS_LISTENER = "PROGRESS_LISTENER";

    public static final String JOB_EXECUTION_LISTENER = "JOB_EXECUTION_LISTENER";

    public static final String ACCESSIONING_SHUTDOWN_STEP = "ACCESSIONING_SHUTDOWN_STEP";

    public static final String CLUSTERING_FROM_VCF_STEP = "CLUSTERING_FROM_VCF_STEP";

    public static final String CLUSTERING_CLUSTERED_VARIANTS_FROM_MONGO_STEP = "CLUSTERING_CLUSTERED_VARIANTS_FROM_MONGO_STEP";

    public static final String PROCESS_RS_MERGE_CANDIDATES_STEP = "PROCESS_RS_MERGE_CANDIDATES_STEP";

    public static final String PROCESS_RS_SPLIT_CANDIDATES_STEP = "PROCESS_RS_SPLIT_CANDIDATES_STEP";

    public static final String CLEAR_RS_MERGE_AND_SPLIT_CANDIDATES_STEP = "CLEAR_RS_MERGE_AND_SPLIT_CANDIDATES_STEP";

    public static final String CLUSTERING_NON_CLUSTERED_VARIANTS_FROM_MONGO_STEP = "CLUSTERING_NON_CLUSTERED_VARIANTS_FROM_MONGO_STEP";

    public static final String STUDY_CLUSTERING_STEP = "STUDY_CLUSTERING_STEP";

    public static final String BACK_PROPAGATE_NEW_RS_STEP = "BACK_PROPAGATE_NEW_RS_STEP";

    public static final String BACK_PROPAGATE_SPLIT_OR_MERGED_RS_STEP = "BACK_PROPAGATE_SPLIT_OR_MERGED_RS_STEP";

    public static final String CLUSTERING_FROM_VCF_JOB = "CLUSTERING_FROM_VCF_JOB";

    public static final String CLUSTERING_FROM_MONGO_JOB = "CLUSTERING_FROM_MONGO_JOB";

    public static final String STUDY_CLUSTERING_JOB = "STUDY_CLUSTERING_JOB";

    public static final String PROCESS_REMAPPED_VARIANTS_WITH_RS_JOB = "PROCESS_REMAPPED_VARIANTS_WITH_RS_JOB";

    public static final String CLUSTER_UNCLUSTERED_VARIANTS_JOB = "CLUSTER_UNCLUSTERED_VARIANTS_JOB";
    public static final String RESOLVE_MERGE_THEN_SPLIT_CANDIDATE_JOB = "RESOLVE_MERGE_THEN_SPLIT_CANDIDATE_JOB";

    public static final String BACK_PROPAGATE_NEW_RS_JOB = "BACK_PROPAGATE_NEW_RS_JOB";

    public static final String BACK_PROPAGATE_SPLIT_OR_MERGED_RS_JOB = "BACK_PROPAGATE_SPLIT_OR_MERGED_RS_JOB";

    public static final String NON_CLUSTERED_CLUSTERING_WRITER_JOB_EXECUTION_SETTER = "NON_CLUSTERED_CLUSTERING_WRITER_JOB_EXECUTION_SETTER";

    public static final String CLUSTERED_CLUSTERING_WRITER_JOB_EXECUTION_SETTER = "CLUSTERED_CLUSTERING_WRITER_JOB_EXECUTION_SETTER";

    public static final String RS_SPLIT_WRITER_JOB_EXECUTION_SETTER = "RS_SPLIT_WRITER_JOB_EXECUTION_SETTER";

    public static final String RS_ACCESSION_RECOVERY_SERVICE = "RS_ACCESSION_RECOVERY_SERVICE";

    public static final String RS_ACCESSION_RECOVERY_STEP = "RS_ACCESSION_RECOVERY_STEP";

    public static final String RS_ACCESSION_RECOVERY_JOB = "RS_ACCESSION_RECOVERY_JOB";

    public static final String RS_ACCESSION_RECOVERY_JOB_LISTENER = "RS_ACCESSION_RECOVERY_JOB_LISTENER";

    public static final String DUPLICATE_RS_ACC_QC_FILE_READER = "DUPLICATE_RS_ACC_QC_FILE_READER";

    public static final String DUPLICATE_RS_ACC_QC_PROCESSOR = "DUPLICATE_RS_ACC_QC_PROCESSOR";

    public static final String DUPLICATE_RS_ACC_QC_WRITER = "DUPLICATE_RS_ACC_QC_WRITER";

    public static final String DUPLICATE_RS_ACC_QC_JOB = "DUPLICATE_RS_ACC_QC_JOB";

    public static final String DUPLICATE_RS_ACC_QC_STEP = "DUPLICATE_RS_ACC_QC_STEP";
}
