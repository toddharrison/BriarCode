plugins {
    `maven-publish`
}

configure<PublishingExtension> {
    repositories {
        maven {
            url = uri("$buildDir/local-repository")
        }
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/toddharrison/BriarCode")
//            credentials {
//                username = System.getenv("GITHUB_ACTOR")
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
    }
}
