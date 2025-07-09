package util

import config.TestAppConfig
import java.lang.reflect.Field

object TestConfigUtil {
    
    private val originalEnvVars = mutableMapOf<String, String?>()
    
    fun setupTestEnvironment() {
        // 環境変数をテスト用に設定
        setEnv("JWT_SECRET", TestAppConfig.Jwt.secret)
        setEnv("JWT_ISSUER", TestAppConfig.Jwt.issuer)
        setEnv("JWT_EXPIRES_IN_HOURS", TestAppConfig.Jwt.expiresInHours.toString())
        setEnv("ENVIRONMENT", TestAppConfig.Server.environment)
        setEnv("SERVER_HOST", TestAppConfig.Server.host)
        setEnv("SERVER_PORT", TestAppConfig.Server.port.toString())
        setEnv("POSTGRES_HOST", TestAppConfig.Database.host)
        setEnv("POSTGRES_PORT", TestAppConfig.Database.port)
        setEnv("POSTGRES_DB", TestAppConfig.Database.name)
        setEnv("POSTGRES_USER", TestAppConfig.Database.user)
        setEnv("POSTGRES_PASSWORD", TestAppConfig.Database.password)
        setEnv("LOG_LEVEL", TestAppConfig.Logging.level)
    }
    
    fun clearTestEnvironment() {
        // 元の環境変数を復元
        originalEnvVars.forEach { (key, value) ->
            if (value == null) {
                removeEnv(key)
            } else {
                setEnv(key, value)
            }
        }
        originalEnvVars.clear()
    }
    
    private fun setEnv(key: String, value: String) {
        // 元の値を保存
        if (!originalEnvVars.containsKey(key)) {
            originalEnvVars[key] = System.getenv(key)
        }
        
        try {
            val env = System.getenv()
            val cl = env.javaClass
            val field = cl.getDeclaredField("m")
            field.isAccessible = true
            val writableEnv = field.get(env) as MutableMap<String, String>
            writableEnv[key] = value
        } catch (e: Exception) {
            // Fallback: system property
            System.setProperty(key, value)
        }
    }
    
    private fun removeEnv(key: String) {
        try {
            val env = System.getenv()
            val cl = env.javaClass
            val field = cl.getDeclaredField("m")
            field.isAccessible = true
            val writableEnv = field.get(env) as MutableMap<String, String>
            writableEnv.remove(key)
        } catch (e: Exception) {
            // Fallback: system property
            System.clearProperty(key)
        }
    }
}