import gradle.kotlin.dsl.accessors._8fc9d5def6cfbac1518e42bb3c447917.publishing

plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
//    repositories {
////        maven {
////            url = uri("$buildDir/local-repository")
////        }
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/toddharrison/BriarCode")
//            credentials {
//                username = System.getenv("GITHUB_ACTOR")
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
}
