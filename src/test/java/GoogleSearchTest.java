import com.microsoft.playwright.Page;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Ignore
public class GoogleSearchTest extends BasePlayWrightTest{


    @Step ("Open page and search")
    public void openBrowser(){
        page.navigate("https://www.google.com/");
        assertThat(page.getByLabel("Пошук", new Page.GetByLabelOptions().setExact(true))).isVisible();
    }

    @Step ("Enter text into search filed")
    public void enterIntoSearchField(String search){
        page.getByLabel("Пошук", new Page.GetByLabelOptions().setExact(true)).fill(search);
    }

    @Test
    @Description ("Поиск в гугле")
    @Severity(SeverityLevel.CRITICAL)
    public void searchTest(){
       openBrowser();
       enterIntoSearchField("hi google");
        page.getByLabel("Пошук", new Page.GetByLabelOptions().setExact(true)).press("Enter");
        assertThat(page.locator("#result-stats")).containsText("Приблизна кількість результатів:");
        assertThat(page.locator("#center_col")).containsText("google");
        Assert.assertTrue(page.locator("#center_col").isVisible());
    }
}
