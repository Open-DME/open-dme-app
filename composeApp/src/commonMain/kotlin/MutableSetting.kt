import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlin.reflect.KProperty

class MutableSettings(
    private val settings: Settings = Settings(),
    private val defaultValue: String
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return settings[property.name, defaultValue]
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        if(value == defaultValue) {
            settings.remove(property.name)
        } else {
            settings.putString(property.name, value)
        }
    }
}

fun mutableSetting(initialValue: String = ""): MutableSettings {
    return MutableSettings(defaultValue = initialValue)
}