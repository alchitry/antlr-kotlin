plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api("org.antlr:antlr4:${Versions.antlr}")
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
