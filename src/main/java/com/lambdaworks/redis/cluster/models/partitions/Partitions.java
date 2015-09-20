package com.lambdaworks.redis.cluster.models.partitions;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.lambdaworks.redis.cluster.SlotHash;

/**
 * Cluster topology view. An instance of {@link Partitions} provides access to the partitions of a Redis Cluster. A partition is
 * represented by a Redis Cluster node that has a {@link RedisClusterNode#getNodeId() nodeId} and
 * {@link RedisClusterNode#getUri() connection point details}.
 * <p>
 * Partitions can be looked up by {@code nodeId} or {@code slot} (masters only). A nodeId can be migrated to a different host.
 * Partitions are cached to ensure a cheap lookup by {@code slot}. Users of {@link Partitions} are required to call
 * {@link #updateCache()} after topology changes occur.
 * </p>
 * 
 * Topology changes are:
 * 
 * <ul>
 * <li>Changes in {@link com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode.NodeFlag#MASTER}/
 * {@link com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode.NodeFlag#SLAVE} state</li>
 * <li>Newly added or removed nodes to/from the Redis Cluster</li>
 * <li>Changes in {@link RedisClusterNode#getSlots()} responsibility</li>
 * <li>Changes to the {@link RedisClusterNode#getSlaveOf() slave replication source} (the master of a slave)</li>
 * <li>Changes to the {@link RedisClusterNode#getUri()} () connection point}</li>
 * </ul>
 * 
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
public class Partitions extends AbstractCollection<RedisClusterNode> implements Collection<RedisClusterNode> {

    private List<RedisClusterNode> partitions = Lists.newArrayList();
    private RedisClusterNode slotCache[];

    /**
     * Retrieve a {@link RedisClusterNode} by its slot number. This method does not distinguish between masters and slaves.
     *
     * @param slot the slot
     * @return RedisClusterNode or {@literal null}
     */
    public RedisClusterNode getPartitionBySlot(int slot) {
        return slotCache[slot];
    }

    /**
     * Retrieve a {@link RedisClusterNode} by its node id.
     *
     * @param nodeId the nodeId
     * @return RedisClusterNode or {@literal null}
     */
    public RedisClusterNode getPartitionByNodeId(String nodeId) {
        for (RedisClusterNode partition : partitions) {
            if (partition.getNodeId().equals(nodeId)) {
                return partition;
            }
        }
        return null;
    }

    /**
     * Update the partition cache. Updates are necessary after the partition details have changed.
     */
    public synchronized void updateCache() {
        if (slotCache == null) {
            slotCache = new RedisClusterNode[SlotHash.SLOT_COUNT];
        } else {
            Arrays.fill(slotCache, null);
        }

        for (RedisClusterNode partition : partitions) {
            for (Integer integer : partition.getSlots()) {
                slotCache[integer.intValue()] = partition;
            }
        }
    }

    @Override
    public Iterator<RedisClusterNode> iterator() {
        return partitions.iterator();
    }

    public List<RedisClusterNode> getPartitions() {
        return partitions;
    }

    public void addPartition(RedisClusterNode partition) {
        slotCache = null;
        partitions.add(partition);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ").append(partitions);
        return sb.toString();
    }

    @Override
    public int size() {
        return getPartitions().size();
    }

    public RedisClusterNode getPartition(int index) {
        return getPartitions().get(index);
    }

    /**
     * Update partitions and rebuild slot cache.
     *
     * @param partitions list of new partitions
     */
    public void reload(List<RedisClusterNode> partitions) {
        this.partitions.clear();
        this.partitions.addAll(partitions);
        updateCache();
    }
}
