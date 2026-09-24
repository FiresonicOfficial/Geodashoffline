package com.example.model

import org.json.JSONArray
import org.json.JSONObject

data class Level(
    val id: String,
    val name: String,
    val description: String,
    val difficulty: Difficulty,
    val stars: Int,
    val author: String,
    val isCustom: Boolean,
    val musicTrack: Int = 0, // 0: Stereo Track, 1: Cyber Beat, 2: Dark Demon, 3: Neon Pulse
    val bgColor: Long = 0xFF0D1117,
    val groundColor: Long = 0xFF003366,
    val objects: List<GameObject> = emptyList(),
    val bestPercentage: Int = 0,
    val completed: Boolean = false,
    val attempts: Int = 0,
    val jumps: Int = 0,
    val coinsCollected: Int = 0,
    val userRating: Float = 0f, // 1.0 to 5.0
    val userRatingsCount: Int = 0,
    val likesCount: Int = 0,
    val isUnlocked: Boolean = true,
    val highScore: Long = 0L,
    val unlockRequirement: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalCoinsInLevel: Int
        get() = objects.count { it.type == ObjectType.COIN }

    val lengthInGridUnits: Float
        get() = (objects.maxOfOrNull { it.x + it.type.width } ?: 50f) + 15f

    companion object {
        fun serializeObjects(objects: List<GameObject>): String {
            val jsonArray = JSONArray()
            for (obj in objects) {
                val item = JSONObject()
                item.put("x", obj.x)
                item.put("y", obj.y)
                item.put("t", obj.type.name)
                if (obj.customColorHex != null) {
                    item.put("c", obj.customColorHex)
                }
                jsonArray.put(item)
            }
            return jsonArray.toString()
        }

        fun deserializeObjects(jsonString: String): List<GameObject> {
            if (jsonString.isBlank()) return emptyList()
            val list = mutableListOf<GameObject>()
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val x = item.getDouble("x").toFloat()
                    val y = item.getDouble("y").toFloat()
                    val typeName = item.getString("t")
                    val type = try {
                        ObjectType.valueOf(typeName)
                    } catch (e: Exception) {
                        ObjectType.BLOCK
                    }
                    val customColor = if (item.has("c")) item.getLong("c") else null
                    list.add(GameObject(x = x, y = y, type = type, customColorHex = customColor))
                }
            } catch (e: Exception) {
                // Fallback empty if corrupt
            }
            return list
        }

        fun exportLevelCode(level: Level): String {
            val json = JSONObject()
            json.put("v", 1)
            json.put("n", level.name)
            json.put("d", level.description)
            json.put("diff", level.difficulty.name)
            json.put("s", level.stars)
            json.put("a", level.author)
            json.put("m", level.musicTrack)
            json.put("bg", level.bgColor)
            json.put("gr", level.groundColor)
            json.put("objs", serializeObjects(level.objects))
            return android.util.Base64.encodeToString(json.toString().toByteArray(), android.util.Base64.NO_WRAP)
        }

        fun importLevelCode(code: String): Level? {
            return try {
                val decoded = String(android.util.Base64.decode(code.trim(), android.util.Base64.NO_WRAP))
                val json = JSONObject(decoded)
                val objects = deserializeObjects(json.getString("objs"))
                Level(
                    id = "custom_" + System.currentTimeMillis(),
                    name = json.optString("n", "Imported Level"),
                    description = json.optString("d", "Imported custom level"),
                    difficulty = Difficulty.fromString(json.optString("diff", "NORMAL")),
                    stars = json.optInt("s", 3),
                    author = json.optString("a", "Community"),
                    isCustom = true,
                    musicTrack = json.optInt("m", 0),
                    bgColor = json.optLong("bg", 0xFF0D1117),
                    groundColor = json.optLong("gr", 0xFF003366),
                    objects = objects,
                    createdAt = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
