package fr.treeptik.cloudunit.cli.shell;

import com.github.lalyos.jfiglet.FigletFont;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

/**
 * Created by guillaume on 15/10/15.
 */
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CloudUnitBannerProvider implements BannerProvider, CommandMarker {

    @Value("${cli.version}")
    private String cliVersion;

    @Override
    public String getBanner() {
        return FigletFont.convertOneLine("CloudUnit-CLI");
    }

    @Override
    public String getVersion() {
        return cliVersion;
    }

    @Override
    public String getWelcomeMessage() {
        return "CloudUnit " + cliVersion + " CLI - Create, deploy and manage your JAVA application into the Cloud";
    }

    @Override
    public String getProviderName() {
        return "cloudunit-cli";
    }
}
