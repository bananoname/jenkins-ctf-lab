import jenkins.model.*
import hudson.security.*
import java.util.logging.Logger
import java.util.Random

def logger = Logger.getLogger("")

// Cài đặt plugin
def instance = Jenkins.getInstanceOrNull()
if (instance == null) {
    throw new IllegalStateException("Jenkins instance is not available")
}

def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

// Kích hoạt Update Center và cập nhật danh sách plugins
updateCenter.updateAllSites()

// Danh sách plugins cần cài đặt
def requiredPlugins = [
    "git",
    "workflow-aggregator",
    "credentials",
    "docker-plugin",
    "job-dsl",
    "configuration-as-code",
    "pipeline-stage-view",
    "blueocean"
    // Thêm các plugins khác nếu cần thiết
]

// Cài đặt các plugins
requiredPlugins.each { pluginShortName ->
    if (!pluginManager.getPlugin(pluginShortName)) {
        def plugin = updateCenter.getPlugin(pluginShortName)
        if (plugin) {
            plugin.deploy().get()
            logger.info("Installed plugin: ${pluginShortName}")
        } else {
            logger.warning("Plugin not found: ${pluginShortName}")
        }
    } else {
        logger.info("Plugin already installed: ${pluginShortName}")
    }
}

// Đợi các plugins được cài đặt hoàn toàn
instance.save()

// Cấu hình bảo mật
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin") // Username: admin, Password: admin
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

instance.save()

// Tạo thư mục lưu trữ flags
def flagsDirPath = "${instance.getRootDir()}/flags"
def flagsDir = new File(flagsDirPath)
if (!flagsDir.exists()) {
    flagsDir.mkdirs()
    logger.info("Created flags directory at: ${flagsDirPath}")
} else {
    logger.info("Flags directory already exists at: ${flagsDirPath}")
}

// Hàm tạo một flag ngẫu nhiên
def generateFlag() {
    def chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    def flag = 'FLAG{' + (1..16).collect { chars[new Random().nextInt(chars.size())] }.join() + '}'
    return flag
}

// Hàm tạo Pipeline Job cho CTF
def createCTFJob(jobName, challengeDescription, isWebChallenge = false) {
    if (Jenkins.instance.getItem(jobName)) {
        logger.info("Job already exists: ${jobName}")
        return
    }

    def job = Jenkins.instance.createProject(org.jenkinsci.plugins.workflow.job.WorkflowJob, jobName)
    if (isWebChallenge) {
        job.definition = new org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition(
            """
            pipeline {
                agent any
                stages {
                    stage('Challenge Description') {
                        steps {
                            echo '${challengeDescription}'
                        }
                    }
                    stage('Execute Challenge') {
                        steps {
                            // Thêm các bước thử thách cụ thể tại đây
                            echo 'Hãy truy cập trang chủ để tìm kiếm flag.'
                        }
                    }
                    stage('Complete Challenge') {
                        steps {
                            script {
                                // Kiểm tra điều kiện hoàn thành thách thức
                                // Nếu hoàn thành, hiển thị flag
                                def flagFile = "${flagsDirPath}/${jobName}/flag.txt"
                                if (fileExists(flagFile)) {
                                    def flag = readFile(flagFile).trim()
                                    echo "Chúc mừng! Flag của bạn là: \${flag}"
                                } else {
                                    echo "Không tìm thấy flag file!"
                                }
                            }
                        }
                    }
                }
                post {
                    success {
                        echo 'Challenge Completed Successfully!'
                    }
                    failure {
                        echo 'Challenge Failed!'
                    }
                }
            }
            """,
            true
        )
    } else {
        job.definition = new org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition(
            """
            pipeline {
                agent any
                stages {
                    stage('Challenge Description') {
                        steps {
                            echo '${challengeDescription}'
                        }
                    }
                    stage('Execute Challenge') {
                        steps {
                            echo 'Running the challenge...'
                            // Giả lập một bước thực thi thành công
                        }
                    }
                    stage('Complete Challenge') {
                        steps {
                            script {
                                def flagFile = "${flagsDirPath}/${jobName}/flag.txt"
                                if (fileExists(flagFile)) {
                                    def flag = readFile(flagFile).trim()
                                    echo "Chúc mừng! Flag của bạn là: \${flag}"
                                } else {
                                    echo "Không tìm thấy flag file!"
                                }
                            }
                        }
                    }
                }
                post {
                    success {
                        echo 'Challenge Completed Successfully!'
                    }
                    failure {
                        echo 'Challenge Failed!'
                    }
                }
            }
            """,
            true
        )
    }
    job.save()

    // Tạo thư mục cho challenge và lưu flag vào file
    def challengeDir = new File(flagsDir, jobName)
    if (!challengeDir.exists()) {
        challengeDir.mkdirs()
        logger.info("Created directory for ${jobName} at: ${challengeDir.path}")
    }

    // Tạo flag cho thách thức và lưu vào file
    def flag = generateFlag()
    def flagFile = new File(challengeDir, "flag.txt")
    flagFile.text = flag
    logger.info("Created flag for ${jobName}: ${flag}")
}

// Tạo các thách thức CTF
createCTFJob("CTF_Challenge_1", "Thách thức 1: Khai thác lỗ hổng SQL Injection.")
createCTFJob("CTF_Challenge_2", "Thách thức 2: Xâm nhập vào hệ thống bằng cách khai thác XSS.")
createCTFJob("CTF_Challenge_3", "Thách thức 3: Tìm và khai thác lỗ hổng RCE.")

// Tạo thách thức mới: Dò tìm flag trên trang chủ
createCTFJob("CTF_Homepage_Search", "Thách thức 4: Dò tìm flag trên trang chủ giới thiệu về trường học.", true)

instance.save()

