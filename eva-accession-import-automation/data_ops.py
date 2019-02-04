import psycopg2


def get_pg_conn_for_species(species_info):
    return psycopg2.connect("dbname='dbsnp_{0}' user='{1}' host='{2}' port='{3}'".
                            format(species_info["dbsnp_build"], "dbsnp", species_info["pg_host"],
                                   species_info["pg_port"]))


def get_contiginfo_table_list_for_schema(pg_cursor, schema_name):
    pg_cursor.execute(
        """
        select string_agg(table_name,',' order by table_name) from information_schema.tables where 
        table_schema = 'dbsnp_{0}' and table_name like 'b%contiginfo';
        """.format(schema_name.lower())
    )
    results = pg_cursor.fetchall()
    if not results:
        return None
    return results[0][0].split(",")


def get_species_pg_conn_info(pg_metadata_dbname, pg_metadata_user, pg_metadata_host):
    """
    Get Postgres connection information for all the mirrored dbSNP species

    :param pg_metadata_dbname: Metadata database name
    :param pg_metadata_user: Metadata user name
    :param pg_metadata_host: Host where the metadata database resides
    :return: List of dictionaries with connection information for each species
    """
    pg_conn = psycopg2.connect("dbname='{0}' user='{1}' host='{2}'".
                               format(pg_metadata_dbname, pg_metadata_user, pg_metadata_host))
    pg_cursor = pg_conn.cursor()
    pg_cursor.execute("select database_name,dbsnp_build,pg_host,pg_port from dbsnp_ensembl_species.import_progress a "
                      "join dbsnp_ensembl_species.dbsnp_build_instance b on b.dbsnp_build = a.ebi_pg_dbsnp_build")
    species_set = [{"database_name": result[0], "dbsnp_build":result[1], "pg_host":result[2], "pg_port":result[3]}
                   for result in pg_cursor.fetchall()]
    pg_cursor.close()
    pg_conn.close()
    return species_set


def get_species_info(pg_metadata_dbname, pg_metadata_user, pg_metadata_host,
                     assembly_accession):
    """
    Get information about an assembly accession

    :param pg_metadata_dbname: Metadata database name
    :param pg_metadata_user: Metadata user name
    :param pg_metadata_host: Host where the metadata database resides
    :param assembly_accession: species to query, e.g. GCA_000001735.1
    :return: Dictionary with species information
    """
    pg_conn = psycopg2.connect("dbname='{0}' user='{1}' host='{2}'".
                               format(pg_metadata_dbname, pg_metadata_user, pg_metadata_host))
    pg_cursor = pg_conn.cursor()
    pg_cursor.execute("select database_name, tax_id from dbsnp_ensembl_species.import_progress i "
                      " where genbank_assembly_accession = '{}'".format(assembly_accession))
    species = [{"database_name": result[0], "taxonomy": result[1]}
               for result in pg_cursor.fetchall()]
    pg_cursor.close()
    pg_conn.close()
    if len(species) == 0:
        raise Exception("No species with assembly {} in table import_progress".format(assembly_accession))
    if len(species) > 1:
        raise Exception("More than one species with assembly {} in table import_progress".format(assembly_accession))
    return species[0]


def get_assembly_name(species_db_info, build):
    pg_conn = get_pg_conn_for_species(species_db_info)
    pg_cursor = pg_conn.cursor()
    contiginfo_tables = get_contiginfo_table_list_for_schema(pg_cursor, species_db_info["database_name"])

    table_name = "dbsnp_{}.b{}_contiginfo".format(species_db_info["database_name"], build)
    pg_cursor.execute("select distinct group_label from {}".format(table_name))
    assembly_names = pg_cursor.fetchall()
    pg_cursor.close()
    pg_conn.close()
    if len(assembly_names) == 0:
        raise Exception("No assembly names found in table {}".format(table_name))
    if len(assembly_names) > 1:
        raise Exception("More than one assembly names found in table {}: {}"
                        .format(table_name, [result[0] for result in assembly_names]))
    return assembly_names[0][0]
