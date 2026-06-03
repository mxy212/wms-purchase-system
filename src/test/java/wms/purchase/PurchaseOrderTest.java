package wms.purchase;

public class PurchaseOrderTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== 正常流程测试 ==========");
        run("正常采购付款流程", PurchaseOrderTest::normalPaymentFlow);
        run("正常退货退款流程", PurchaseOrderTest::normalReturnFlow);
        run("草稿编辑后审批入库", PurchaseOrderTest::editThenApproveAndInbound);

        System.out.println("\n========== 异常拦截测试 ==========");
        run("未审批禁止入库", () -> expectFailure(newOrder()::generateInboundOrder, "不允许执行【生成入库单】"));
        run("未入库禁止开票", () -> expectFailure(newOrder()::generateInvoice, "不允许执行【生成发票】"));
        run("未开票禁止付款", () -> expectFailure(newOrder()::pay, "不允许执行【付款】"));
        run("未入库禁止退货", () -> expectFailure(newOrder()::returnAndRefund, "不允许执行【退货退款】"));
        run("已入库禁止取消", PurchaseOrderTest::cannotCancelAfterInbound);
        run("已开票禁止取消", PurchaseOrderTest::cannotCancelAfterInvoice);
        run("已付款禁止取消", PurchaseOrderTest::cannotCancelAfterPaid);
        run("审批后禁止编辑", PurchaseOrderTest::cannotEditAfterApproval);
        run("已取消禁止后续操作", PurchaseOrderTest::cancelledOrderStopsFlow);
        run("已付款禁止所有操作", PurchaseOrderTest::paidOrderStopsFlow);
        run("已退货完结禁止所有操作", PurchaseOrderTest::returnedOrderStopsFlow);

        System.out.println("\n========== 边界条件测试 ==========");
        run("草稿状态可多次编辑", PurchaseOrderTest::multipleEditsInDraft);
        run("草稿状态可取消", PurchaseOrderTest::canCancelInDraft);
        run("已审批状态可取消", PurchaseOrderTest::canCancelAfterApproval);
        run("采购单基本信息查询", PurchaseOrderTest::queryBasicInfo);
        run("操作日志完整性验证", PurchaseOrderTest::verifyOperationLogs);

        System.out.println("\n========== 参数校验测试 ==========");
        run("空商品名称拒绝创建", PurchaseOrderTest::rejectEmptyProductName);
        run("零数量拒绝创建", PurchaseOrderTest::rejectZeroQuantity);
        run("负数数量拒绝创建", PurchaseOrderTest::rejectNegativeQuantity);
        run("编辑时零数量拒绝", PurchaseOrderTest::rejectZeroQuantityOnEdit);

        System.out.println("\n====================================");
        System.out.println("测试完成：通过 " + passed + " 项，失败 " + failed + " 项。");
        System.out.println("====================================");
        if (failed > 0) {
            throw new AssertionError("存在失败测试。");
        }
    }

    private static PurchaseOrder newOrder() {
        return new PurchaseOrder("PO-20260603-001", "工业扫码枪", 10, "华东供应商");
    }

    private static void normalPaymentFlow() {
        PurchaseOrder order = newOrder();
        order.edit("工业扫码枪", 20, "华东供应商");
        order.approve();
        order.generateInboundOrder();
        order.generateInvoice();
        order.pay();
        assertEquals("已付款", order.getStateName());
        assertEquals("IN-PO-20260603-001", order.getInboundOrderNo());
        assertEquals("INV-PO-20260603-001", order.getInvoiceNo());
        System.out.println("最终状态: " + order);
        System.out.println("操作日志:");
        order.getLogs().forEach(log -> System.out.println("  " + log));
    }

    private static void normalReturnFlow() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        order.returnAndRefund();
        assertEquals("已退货完结", order.getStateName());
        assertEquals("RT-PO-20260603-001", order.getReturnOrderNo());
        System.out.println("退货流程完成: " + order);
    }

    private static void editThenApproveAndInbound() {
        PurchaseOrder order = newOrder();
        order.edit("升级版扫码枪", 50, "华南供应商");
        assertEquals("升级版扫码枪", order.getProduct());
        assertEquals(50, order.getQuantity());
        assertEquals("华南供应商", order.getSupplier());
        order.approve();
        assertEquals("已审批", order.getStateName());
        order.generateInboundOrder();
        assertEquals("已入库", order.getStateName());
        System.out.println("编辑-审批-入库流程验证通过");
    }

    private static void cannotCancelAfterInbound() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        expectFailure(order::cancel, "不允许执行【取消】");
    }

    private static void cannotCancelAfterInvoice() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        order.generateInvoice();
        expectFailure(order::cancel, "不允许执行【取消】");
    }

    private static void cannotCancelAfterPaid() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        order.generateInvoice();
        order.pay();
        expectFailure(order::cancel, "不允许执行【取消】");
    }

    private static void cancelledOrderStopsFlow() {
        PurchaseOrder order = newOrder();
        order.cancel();
        assertEquals("已取消", order.getStateName());
        expectFailure(order::approve, "不允许执行【审批】");
        expectFailure(order::generateInboundOrder, "不允许执行【生成入库单】");
        expectFailure(order::generateInvoice, "不允许执行【生成发票】");
        expectFailure(order::pay, "不允许执行【付款】");
        expectFailure(order::returnAndRefund, "不允许执行【退货退款】");
    }

    private static void paidOrderStopsFlow() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        order.generateInvoice();
        order.pay();
        expectFailure(() -> order.edit("新商品", 1, "新供应商"), "不允许执行【编辑】");
        expectFailure(order::cancel, "不允许执行【取消】");
        expectFailure(order::returnAndRefund, "不允许执行【退货退款】");
    }

    private static void returnedOrderStopsFlow() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.generateInboundOrder();
        order.returnAndRefund();
        expectFailure(() -> order.edit("新商品", 1, "新供应商"), "不允许执行【编辑】");
        expectFailure(order::generateInvoice, "不允许执行【生成发票】");
        expectFailure(order::pay, "不允许执行【付款】");
    }

    private static void cannotEditAfterApproval() {
        PurchaseOrder order = newOrder();
        order.approve();
        expectFailure(() -> order.edit("新商品", 1, "新供应商"), "不允许执行【编辑】");
    }

    private static void multipleEditsInDraft() {
        PurchaseOrder order = newOrder();
        order.edit("商品A", 10, "供应商A");
        order.edit("商品B", 20, "供应商B");
        order.edit("商品C", 30, "供应商C");
        assertEquals("商品C", order.getProduct());
        assertEquals(30, order.getQuantity());
        assertEquals("供应商C", order.getSupplier());
        System.out.println("多次编辑验证通过");
    }

    private static void canCancelInDraft() {
        PurchaseOrder order = newOrder();
        order.cancel();
        assertEquals("已取消", order.getStateName());
        System.out.println("草稿状态取消验证通过");
    }

    private static void canCancelAfterApproval() {
        PurchaseOrder order = newOrder();
        order.approve();
        order.cancel();
        assertEquals("已取消", order.getStateName());
        System.out.println("已审批状态取消验证通过");
    }

    private static void queryBasicInfo() {
        PurchaseOrder order = newOrder();
        assertEquals("PO-20260603-001", order.getOrderNo());
        assertEquals("工业扫码枪", order.getProduct());
        assertEquals(10, order.getQuantity());
        assertEquals("华东供应商", order.getSupplier());
        assertEquals("草稿", order.getStateName());
        System.out.println("基本信息查询验证通过: " + order);
    }

    private static void verifyOperationLogs() {
        PurchaseOrder order = newOrder();
        int initialLogCount = order.getLogs().size();
        order.edit("新商品", 20, "新供应商");
        order.approve();
        order.generateInboundOrder();
        int finalLogCount = order.getLogs().size();
        if (finalLogCount != initialLogCount + 3) {
            throw new AssertionError("操作日志记录不完整");
        }
        System.out.println("操作日志数量验证通过，共 " + finalLogCount + " 条记录");
    }

    private static void rejectEmptyProductName() {
        try {
            new PurchaseOrder("PO-001", "", 10, "供应商");
            throw new AssertionError("应该拒绝空商品名称");
        } catch (PurchaseOrderException e) {
            System.out.println("已拦截空商品名称: " + e.getMessage());
        }
    }

    private static void rejectZeroQuantity() {
        try {
            new PurchaseOrder("PO-001", "商品", 0, "供应商");
            throw new AssertionError("应该拒绝零数量");
        } catch (PurchaseOrderException e) {
            System.out.println("已拦截零数量: " + e.getMessage());
        }
    }

    private static void rejectNegativeQuantity() {
        try {
            new PurchaseOrder("PO-001", "商品", -5, "供应商");
            throw new AssertionError("应该拒绝负数数量");
        } catch (PurchaseOrderException e) {
            System.out.println("已拦截负数数量: " + e.getMessage());
        }
    }

    private static void rejectZeroQuantityOnEdit() {
        PurchaseOrder order = newOrder();
        expectFailure(() -> order.edit("商品", 0, "供应商"), "采购数量必须大于 0");
    }

    private static void expectFailure(Runnable action, String expectedText) {
        try {
            action.run();
            throw new AssertionError("预期抛出业务异常，但操作成功。");
        } catch (PurchaseOrderException ex) {
            if (!ex.getMessage().contains(expectedText)) {
                throw new AssertionError("异常信息不符合预期：" + ex.getMessage());
            }
            System.out.println("✓ 已拦截：" + ex.getMessage());
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("预期 " + expected + "，实际 " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("预期 " + expected + "，实际 " + actual);
        }
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable ex) {
            failed++;
            System.out.println("[FAIL] " + name + "：" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
