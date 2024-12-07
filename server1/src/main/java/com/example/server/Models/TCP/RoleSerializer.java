package com.example.server.Models.TCP;

import com.example.server.Models.Entities.Role;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RoleSerializer implements JsonSerializer<Role> {
    @Override
    public JsonElement serialize(Role role, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", role.getId());
        jsonObject.addProperty("name", role.getName());
        return jsonObject;
    }
}

