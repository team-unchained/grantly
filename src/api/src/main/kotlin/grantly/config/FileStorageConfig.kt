package grantly.config

import grantly.common.core.store.FileSystemStorage
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FileStorageConfig {
    @Value("\${storage.root-dir:/tmp/grantly-storage}")
    private lateinit var rootDir: String

    @Bean
    fun fileSystemStorage(): FileSystemStorage = FileSystemStorage(rootDir)
}
