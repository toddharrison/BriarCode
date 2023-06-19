plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
//        maven {
//            name = "Local"
//            url = uri("$buildDir/local-repository")
//        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/toddharrison/Demo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
