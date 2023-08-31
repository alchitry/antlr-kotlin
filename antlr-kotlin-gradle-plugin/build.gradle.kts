plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation("org.antlr:antlr4:${Versions.antlr}")
    implementation(gradleApi())
    implementation(project(":antlr-kotlin-target"))
}

publishing {
    publications {
        register("maven", MavenPublication::class) {
            artifactId = project.name
            groupId = project.group as String
            version = project.version as String
            from(components["java"])
        }
    }
}