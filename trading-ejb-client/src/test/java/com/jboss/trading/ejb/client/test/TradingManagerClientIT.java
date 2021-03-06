package com.jboss.trading.ejb.client.test;

import com.jboss.trading.api.TradingManager;
import com.jboss.trading.api.exception.LimitOrderNotFoundException;
import com.jboss.trading.api.exception.MarketOrderNotFoundException;
import com.jboss.trading.api.exception.PlaceOrderException;
import com.jboss.trading.api.model.LimitOrder;
import com.jboss.trading.api.model.MarketOrder;
import com.jboss.trading.api.model.TransactionType;
import com.jboss.trading.ejb.client.TradingManagerClient;
import com.jboss.trading.test.TestConfig;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.NamingException;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingManagerClientIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingManagerClientIT.class);
    
    private TestConfig testConfig;
    
    private ClientTestConfig clientTestConfig;
    
    private TradingManager tradingManager;

    public TradingManagerClientIT() {

        testConfig = TestConfig.getInstance();

        clientTestConfig = ClientTestConfig.getInstance();

        String jndiName = clientTestConfig.getJndiName();

        try {

        	tradingManager = TradingManagerClient.getInstance(jndiName);
        } 
        catch (NamingException ex) {

            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPlaceBuyLimitOrder() throws Exception {

        try {

        	tradingManager.placeLimitOrder(testConfig.getStockHolderId(),
                    TransactionType.BUY, testConfig.getQuantity(),
                    testConfig.getStockSymbol(), testConfig.getPrice());
        } 
        catch (PlaceOrderException ex) {

            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testPlaceBuyMarketOrder() throws Exception {

    	tradingManager.placeMarketOrder(testConfig.getStockHolderId(),
                TransactionType.BUY, testConfig.getQuantity(),
                testConfig.getStockSymbol());
    }

    @Test
    public void testPlaceSellLimitOrder() throws Exception {

        try {

        	tradingManager.placeLimitOrder(testConfig.getStockHolderId(),
                    TransactionType.SELL, testConfig.getQuantity(),
                    testConfig.getStockSymbol(), testConfig.getPrice());
        } 
        catch (PlaceOrderException ex) {

            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testPlaceSellMarketOrder() throws Exception {

    	tradingManager.placeMarketOrder(testConfig.getStockHolderId(),
                TransactionType.SELL, testConfig.getQuantity(),
                testConfig.getStockSymbol());
    }

    @Test
    public void testViewLimitOrder() throws Exception {

        AtomicInteger limitOrderIdCounter = testConfig.getLimitOrderIdCounter();

        LimitOrder limitOrder = tradingManager.viewLimitOrder(limitOrderIdCounter.getAndIncrement());

        Assert.assertNotNull(limitOrder);
    }

    @Test
    public void testViewMarketOrder() throws Exception {

        AtomicInteger marketOrderIdCounter = testConfig.getMarketOrderIdCounter();

        MarketOrder marketOrder = tradingManager.viewMarketOrder(marketOrderIdCounter.getAndIncrement());

        Assert.assertNotNull(marketOrder);
    }

    @Test
    public void testViewStockHolderLimitOrders() throws Exception {

        List<LimitOrder> limitOrders =
        		tradingManager.viewStockHolderLimitOrders(testConfig.getStockHolderId(), testConfig.getMaxLimitOrderResults());

        Assert.assertNotNull(limitOrders);
        Assert.assertFalse(limitOrders.isEmpty());
    }

    @Test
    public void testViewStockHolderMarketOrders() throws Exception {

        List<MarketOrder> marketOrders =
        		tradingManager.viewStockHolderMarketOrders(testConfig.getStockHolderId(), testConfig.getMaxMarketOrderResults());

        Assert.assertNotNull(marketOrders);
        Assert.assertFalse(marketOrders.isEmpty());
    }

    @Test
    public void testCancelLimitOrder() throws Exception {

        AtomicInteger limitOrderIdCounter = testConfig.getLimitOrderIdCounter();

        try {

        	tradingManager.cancelLimitOrder(limitOrderIdCounter.decrementAndGet());
        } 
        catch (LimitOrderNotFoundException ex) {

            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testCancelMarketOrder() throws Exception {

        AtomicInteger marketOrderIdCounter = testConfig.getMarketOrderIdCounter();

        try {

        	tradingManager.cancelMarketOrder(marketOrderIdCounter.decrementAndGet());
        } 
        catch (MarketOrderNotFoundException ex) {

            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testcancelLimitOrderFailure() throws Exception {

        AtomicInteger limitOrderIdCounter = testConfig.getLimitOrderIdCounter();

        int limitOrderId = limitOrderIdCounter.getAndIncrement();

        try {

        	tradingManager.cancelLimitOrder(limitOrderId);

            Assert.fail();
        } 
        catch (LimitOrderNotFoundException ex) {

            Assert.assertEquals(String.format(testConfig.getCancelLimitOrderErrorMsgPrefix(), limitOrderId), ex.getMessage());
        }
    }

    @Test
    public void testcancelMarketOrderFailure() throws Exception {

        AtomicInteger marketOrderCounter = testConfig.getMarketOrderIdCounter();

        int marketOrderId = marketOrderCounter.getAndIncrement();

        try {

        	tradingManager.cancelMarketOrder(marketOrderId);

            Assert.fail();
        } 
        catch (MarketOrderNotFoundException ex) {

            Assert.assertEquals(String.format(testConfig.getCancelMarketOrderErrorMsgPrefix(), marketOrderId), ex.getMessage());
        }
    }
}
