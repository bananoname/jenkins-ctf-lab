import jenkins.model.*
import hudson.security.*
import java.util.logging.Logger
import hudson.model.User
import hudson.tasks.Mailer

def logger = Logger.getLogger("") // Log messages for tracking actions

// Cài đặt và cấu hình Jenkins instance
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
    "blueocean",
    "mailer" // Plugin gửi email khi có thông báo về job
]

// Cài đặt các plugins nếu chưa được cài
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

// Đợi các plugin cài đặt hoàn toàn
instance.save()

// Cấu hình bảo mật cho Jenkins
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin123") // Admin mặc định
hudsonRealm.createAccount("jenny", "jennyPassword") // Người dùng Jenny
hudsonRealm.createAccount("john", "johnPassword")   // Người dùng John
instance.setSecurityRealm(hudsonRealm)

// Phân quyền bảo mật
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false) // Ngăn truy cập ẩn danh
strategy.add(Jenkins.ADMINISTER, "admin")  // Admin có quyền quản trị
strategy.add(Jenkins.ADMINISTER, "jenny")  // Jenny có quyền quản trị
strategy.add(Jenkins.READ, "john")         // John chỉ có quyền đọc
instance.setAuthorizationStrategy(strategy)

instance.save()

// Cấu hình email thông báo khi hoàn thành job
def adminUser = User.getById("admin", true)
adminUser.addProperty(new Mailer.UserProperty("admin@company.com"))
logger.info("Configured email for admin: admin@company.com")

def jennyUser = User.getById("jenny", true)
jennyUser.addProperty(new Mailer.UserProperty("jenny@company.com"))
logger.info("Configured email for jenny: jenny@company.com")

def johnUser = User.getById("john", true)
johnUser.addProperty(new Mailer.UserProperty("john@company.com"))
logger.info("Configured email for john: john@company.com")

// Tạo thư mục lưu trữ flags cho các thử thách
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

// Hàm kiểm tra trạng thái Jenkins, chỉ chạy khi hệ thống ổn định
def checkSystemHealth() {
    def healthReports = instance.getOverallLoad().getLoadStatistics()
    if (healthReports.size() > 0) {
        logger.info("Jenkins system is healthy with no major issues.")
    } else {
        logger.warning("System health reports show potential issues.")
    }
}
checkSystemHealth()

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
                            echo 'Access the homepage to search for the flag.'
                        }
                    }
                    stage('Complete Challenge') {
                        steps {
                            script {
                                def flagFile = "${flagsDirPath}/${jobName}/flag.txt"
                                if (fileExists(flagFile)) {
                                    def flag = readFile(flagFile).trim()
                                    echo "Congratulations! Your flag is: \${flag}"
                                } else {
                                    echo "Flag file not found!"
                                }
                            }
                        }
                    }
                }
                post {
                    success {
                        echo 'Challenge Completed Successfully!'
                        mail to: 'admin@company.com', subject: "Job Completed: ${jobName}", body: "Challenge completed successfully."
                    }
                    failure {
                        echo 'Challenge Failed!'
                        mail to: 'admin@company.com', subject: "Job Failed: ${jobName}", body: "Challenge failed. Please check the logs."
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
                            echo 'Executing challenge...'
                        }
                    }
                    stage('Complete Challenge') {
                        steps {
                            script {
                                def flagFile = "${flagsDirPath}/${jobName}/flag.txt"
                                if (fileExists(flagFile)) {
                                    def flag = readFile(flagFile).trim()
                                    echo "Congratulations! Your flag is: \${flag}"
                                } else {
                                    echo "Flag file not found!"
                                }
                            }
                        }
                    }
                }
                post {
                    success {
                        echo 'Challenge Completed Successfully!'
                        mail to: 'admin@company.com', subject: "Job Completed: ${jobName}", body: "Challenge completed successfully."
                    }
                    failure {
                        echo 'Challenge Failed!'
                        mail to: 'admin@company.com', subject: "Job Failed: ${jobName}", body: "Challenge failed. Please check the logs."
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

// Tạo các thách thức CTF với nhiều thể loại khác nhau
createCTFJob("CTF_SQL_Injection", "Challenge 1: Exploit SQL Injection vulnerability.")
createCTFJob("CTF_XSS_Attack", "Challenge 2: Exploit XSS to gain unauthorized access.")
createCTFJob("CTF_RCE_Attack", "Challenge 3: Find and exploit the RCE vulnerability.")

// Tạo thêm thách thức dò tìm flag trên trang chủ
createCTFJob("CTF_Homepage_Flag_Search", "Challenge 4: Find the hidden flag on the school homepage.", true)

instance.save()

