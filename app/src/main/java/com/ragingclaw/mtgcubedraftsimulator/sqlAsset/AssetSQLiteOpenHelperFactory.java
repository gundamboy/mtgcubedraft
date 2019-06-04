package com.ragingclaw.mtgcubedraftsimulator.sqlAsset;


import androidx.sqlite.db.SupportSQLiteOpenHelper;

/**
 * Implements {@link SupportSQLiteOpenHelper.Factory} using the SQLite implementation in the
 * framework.
 */
@SuppressWarnings("unused")
public class AssetSQLiteOpenHelperFactory implements SupportSQLiteOpenHelper.Factory {
    @Override
    public SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration configuration) {

        return new AssetSQLiteOpenHelper(
                configuration.context, configuration.name, null,
                configuration.callback.version, null, configuration.callback
        );
    }
}
