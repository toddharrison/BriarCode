plugins {
    `maven-publish`
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components["java"])
//            }
//        }
//    }
//}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "Local"
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
