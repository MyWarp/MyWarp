/**
 * This class is generated by jOOQ
 */
package me.taylorkelly.mywarp.dataconnections.generated.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value = { "http://www.jooq.org", "3.4.1" }, comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WorldRecord
        extends
        org.jooq.impl.UpdatableRecordImpl<me.taylorkelly.mywarp.dataconnections.generated.tables.records.WorldRecord>
        implements org.jooq.Record2<org.jooq.types.UInteger, java.util.UUID> {

    private static final long serialVersionUID = 1009632357;

    /**
     * Setter for <code>mywarp.world.world-id</code>.
     */
    public void setWorld_id(org.jooq.types.UInteger value) {
        setValue(0, value);
    }

    /**
     * Getter for <code>mywarp.world.world-id</code>.
     */
    public org.jooq.types.UInteger getWorld_id() {
        return (org.jooq.types.UInteger) getValue(0);
    }

    /**
     * Setter for <code>mywarp.world.world</code>.
     */
    public void setWorld(java.util.UUID value) {
        setValue(1, value);
    }

    /**
     * Getter for <code>mywarp.world.world</code>.
     */
    public java.util.UUID getWorld() {
        return (java.util.UUID) getValue(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.Record1<org.jooq.types.UInteger> key() {
        return (org.jooq.Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.Row2<org.jooq.types.UInteger, java.util.UUID> fieldsRow() {
        return (org.jooq.Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.Row2<org.jooq.types.UInteger, java.util.UUID> valuesRow() {
        return (org.jooq.Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.Field<org.jooq.types.UInteger> field1() {
        return me.taylorkelly.mywarp.dataconnections.generated.tables.World.WORLD.WORLD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.Field<java.util.UUID> field2() {
        return me.taylorkelly.mywarp.dataconnections.generated.tables.World.WORLD.WORLD_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.jooq.types.UInteger value1() {
        return getWorld_id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.UUID value2() {
        return getWorld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldRecord value1(org.jooq.types.UInteger value) {
        setWorld_id(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldRecord value2(java.util.UUID value) {
        setWorld(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldRecord values(org.jooq.types.UInteger value1, java.util.UUID value2) {
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WorldRecord
     */
    public WorldRecord() {
        super(me.taylorkelly.mywarp.dataconnections.generated.tables.World.WORLD);
    }

    /**
     * Create a detached, initialised WorldRecord
     */
    public WorldRecord(org.jooq.types.UInteger world_id, java.util.UUID world) {
        super(me.taylorkelly.mywarp.dataconnections.generated.tables.World.WORLD);

        setValue(0, world_id);
        setValue(1, world);
    }
}
