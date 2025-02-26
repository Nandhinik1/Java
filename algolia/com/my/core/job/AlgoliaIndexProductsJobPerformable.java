package com.my.core.job;

import com.my.core.services.AlgoliaService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlgoliaIndexProductsJobPerformable extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(PushNotificationJobPerformable.class);

    @Autowired
    ProductService productService;

    @Autowired
    AlgoliaService AlgoliaService;

    @Autowired
    CatalogVersionService catalogVersionService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        LOG.info("AlgoliaIndexProductsJob Started...");
       // String productcode = "newproduct";
        catalogVersionService.setSessionCatalogVersion("myProductCatalog", "Online");
        List<ProductModel> products = productService.getAllProductsForCatalogVersion(catalogVersionService.getSessionCatalogVersionForCatalog("myProductCatalog"));

        AlgoliaService.indexProducts(products);
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
