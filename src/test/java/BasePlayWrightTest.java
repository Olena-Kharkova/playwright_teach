import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class BasePlayWrightTest {
    private Browser browser;
    protected Page page;
    private BrowserContext context;
    private Boolean isTraceEnabled = true;
    private Boolean isHeadlessEnabled = false;

    /**
     * Инициализация браузера и его настроек перед запуском всех тестов в классе
     */
    @BeforeClass
    public void setUp() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) isHeadlessEnabled = true;
        //инициализация браузера с настройками
     browser = Playwright
                .create()
                .chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(isHeadlessEnabled).setChannel("chrome"));

        //создаем контекст для браузера
        context = browser.newContext();

        //трейсинг замедляет скорость заполнение полей
        if(isTraceEnabled){
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
        //создаем новую страницу
        page = context.newPage();
    }

    /**
     * Закрывает браузер после выполнения всех тестов в классе
     */
    @AfterClass
    public void tearDown() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
    }

    /**
     * Добавляет вложения к упавшему тесту. Скриншот, исходный код страницы, трейсинг
     *
     * @param result данные о тесте
     * @throws IOException
     */
    @AfterMethod
    public void attachFilesToFailedTest(ITestResult result) throws IOException {
        if (!result.isSuccess()) {
            String uuid = UUID.randomUUID().toString();
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("target/allure-results/screenshot_" + uuid + "screenshot.png"))
                    .setFullPage(true));

            Allure.addAttachment(uuid, new ByteArrayInputStream(screenshot));
            Allure.addAttachment("source.html", "text/html", page.content());

            if (isTraceEnabled) {
                String traceFileName = String.format("target/%s_trace.zip", uuid);
                Path tracePath = Paths.get(traceFileName);
                context.tracing()
                        .stop(new Tracing.StopOptions()
                                .setPath(tracePath));
                Allure.addAttachment("trace.zip", new ByteArrayInputStream(Files.readAllBytes(tracePath)));
            }
        }
    }
}
