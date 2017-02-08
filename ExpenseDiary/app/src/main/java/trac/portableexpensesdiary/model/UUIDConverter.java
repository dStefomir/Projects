package trac.portableexpensesdiary.model;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.UUID;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class UUIDConverter
        extends TypeConverter<String, UUID> {

    @Override
    public String getDBValue(UUID model) {
        return model == null ? null : model.toString();
    }

    @Override
    public UUID getModelValue(String data) {
        return data == null ? null : UUID.fromString(data);
    }
}

