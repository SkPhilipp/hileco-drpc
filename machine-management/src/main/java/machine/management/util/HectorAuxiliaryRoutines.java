package machine.management.util;

import com.google.common.primitives.Ints;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hom.EntityManagerImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HectorAuxiliaryRoutines {

    public static final String CASSANDRA_HOST = System.getProperty("CASSANDRA_HOST", "localhost:9160");
    public static final String CASSANDRA_KEYSPACE = System.getProperty("CASSANDRA_KEYSPACE", "MachineKeyspace");
    public static final int CASSANDRA_KEYSPACE_REPLICATION_FACTOR = Ints.tryParse(System.getProperty("CASSANDRA_KEYSPACE_REPLICATION_FACTOR", "1"));
    public static final String CASSANDRA_COLUMN_FAMILY_NAME = System.getProperty("CASSANDRA_COLUMN_FAMILY_NAME", "MachineColumnFamily");

    public static final String HECTOR_CLUSTER_NAME = System.getProperty("HECTOR_CLUSTER_NAME", "MachinePool");
    public static final String HECTOR_SCAN_CLASSPATH_PREFIX = System.getProperty("HECTOR_SCAN_CLASSPATH_PREFIX", "machine.management.model");

    private static final HectorAuxiliaryRoutines INSTANCE;

    private final Cluster cluster;
    private final Keyspace keyspace;
    private final EntityManagerImpl entityManager;

    static {
        INSTANCE = new HectorAuxiliaryRoutines();
    }


    public HectorAuxiliaryRoutines() {
        this.cluster = HFactory.getOrCreateCluster(HECTOR_CLUSTER_NAME, CASSANDRA_HOST);
        this.keyspace = HFactory.createKeyspace(CASSANDRA_KEYSPACE, this.cluster);
        this.entityManager = new EntityManagerImpl(this.keyspace, HECTOR_SCAN_CLASSPATH_PREFIX);
    }

    public static HectorAuxiliaryRoutines getInstance() {
        return INSTANCE;
    }

    public void defineKeyspace() {
        ColumnFamilyDefinition columnFamilyDefinition = HFactory.createColumnFamilyDefinition(CASSANDRA_KEYSPACE, CASSANDRA_COLUMN_FAMILY_NAME, ComparatorType.BYTESTYPE);
        KeyspaceDefinition keyspaceDefinition = HFactory.createKeyspaceDefinition(CASSANDRA_KEYSPACE, ThriftKsDef.DEF_STRATEGY_CLASS, CASSANDRA_KEYSPACE_REPLICATION_FACTOR, Arrays.asList(columnFamilyDefinition));
        this.cluster.addKeyspace(keyspaceDefinition, true);
    }

    public Map<String, ColumnFamilyDefinition> getColumnFamilies() {
        Map<String, ColumnFamilyDefinition> namedColumnFamilyDefinitionMap = new HashMap<>();
        KeyspaceDefinition keyspaceDefinition = this.cluster.describeKeyspace(CASSANDRA_KEYSPACE);
        if (keyspaceDefinition == null) {
            this.defineKeyspace();
            return this.getColumnFamilies();
        } else {
            List<ColumnFamilyDefinition> columnFamilyDefinitions = keyspaceDefinition.getCfDefs();
            for (ColumnFamilyDefinition columnFamilyDefinition : columnFamilyDefinitions) {
                namedColumnFamilyDefinitionMap.put(columnFamilyDefinition.getName(), columnFamilyDefinition);
            }
            return namedColumnFamilyDefinitionMap;
        }
    }

    public void addColumnFamily(String name) {
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(CASSANDRA_KEYSPACE, name);
        this.cluster.addColumnFamily(cfDef, true);
    }

    public void addColumnFamilyIfNotExists(String name) {
        if (!this.getColumnFamilies().containsKey(name)) {
            ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(CASSANDRA_KEYSPACE, name);
            this.cluster.addColumnFamily(cfDef, true);
        }
    }

    public Keyspace getKeyspace() {
        return this.keyspace;
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    public EntityManagerImpl getEntityManager() {
        return this.entityManager;
    }

}
