import org.flywaydb.core.Flyway
import org.jooq.codegen.gradle.CodegenPluginExtension
import org.jooq.codegen.gradle.CodegenTask
import org.jooq.meta.jaxb.Logging
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.testcontainers:postgresql:1.21.4")
        classpath("org.postgresql:postgresql:42.7.8")
        classpath("org.flywaydb:flyway-database-postgresql:11.19.0")
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jooq.codegen)
}

dependencies {
    implementation(project(":common"))
    implementation(libs.jooq)
    jooqCodegen(libs.postgresql.codegen)
}

val jooqOutputDir = layout.buildDirectory.dir("generated/sources/jooq/main")

val jooqDb = PostgreSQLContainer(DockerImageName.parse("postgres:18"))
    .withDatabaseName("backendtemplate")
    .withUsername("test")
    .withPassword("test")

val migrateJooqDb by tasks.registering {
    group = "jooq"
    description = "Start Postgres Testcontainer and run Flyway migrations for jOOQ codegen"

    doLast {
        if (!jooqDb.isRunning) {
            jooqDb.start()
            logger.lifecycle("Started Postgres Testcontainer for jOOQ at ${jooqDb.jdbcUrl}")
        }

        Flyway.configure()
            .dataSource(jooqDb.jdbcUrl, jooqDb.username, jooqDb.password)
            .locations("filesystem:${project.layout.projectDirectory.dir("src/main/resources/db/migration").asFile.absolutePath}")
            .load()
            .migrate()
    }
}

val stopJooqDb by tasks.registering {
    group = "jooq"
    description = "Stop Postgres Testcontainer used for jOOQ codegen"

    doLast {
        if (jooqDb.isRunning) {
            jooqDb.stop()
            logger.lifecycle("Stopped Postgres Testcontainer for jOOQ")
        }
    }
}

jooq {
    configuration {
        logging = Logging.WARN
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                inputSchema = "public"
                includes = ".*"
                excludes = "flyway_schema_history"
            }
            target {
                packageName = "net.spexity.database.model"
                directory = jooqOutputDir.get().asFile.absolutePath
            }
            generate {

            }
            strategy {
                matchers {
                    foreignKeys {
                        foreignKey {
                            expression = "fk_contributor_community_member"
                            pathMethodNameManyToMany {
                                expression = "memberships"
                            }
                        }
                        foreignKey {
                            expression = "fk_contributor_community_membership"
                            pathMethodNameManyToMany {
                                expression = "members"
                            }
                        }
                    }
                }
            }
        }
    }
}

tasks.named<CodegenTask>("jooqCodegen") {
    dependsOn(migrateJooqDb)
    finalizedBy(stopJooqDb)

    doFirst {
        if (!jooqDb.isRunning) {
            throw IllegalStateException("Postgres Testcontainer must be running before jOOQ code generation")
        }

        project.extensions.getByType(CodegenPluginExtension::class.java).configuration {
            jdbc {
                driver = "org.postgresql.Driver"
                url = jooqDb.jdbcUrl
                user = jooqDb.username
                password = jooqDb.password
            }
        }

        logger.lifecycle("Injecting Testcontainers credentials into jOOQ: ${jooqDb.jdbcUrl}")
    }
}

sourceSets.named("main") {
    java.srcDir(jooqOutputDir)
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}
