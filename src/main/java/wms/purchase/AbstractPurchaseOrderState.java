package wms.purchase;

public abstract class AbstractPurchaseOrderState implements PurchaseOrderState {
    protected void reject(String operation) {
        throw new PurchaseOrderException(
                "操作失败：采购单当前状态为【" + getName() + "】，不允许执行【" + operation + "】。");
    }

    @Override
    public void edit(PurchaseOrder order, String product, int quantity, String supplier) {
        reject("编辑");
    }

    @Override
    public void approve(PurchaseOrder order) {
        reject("审批");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        reject("取消");
    }

    @Override
    public void generateInboundOrder(PurchaseOrder order) {
        reject("生成入库单");
    }

    @Override
    public void generateInvoice(PurchaseOrder order) {
        reject("生成发票");
    }

    @Override
    public void pay(PurchaseOrder order) {
        reject("付款");
    }

    @Override
    public void returnAndRefund(PurchaseOrder order) {
        reject("退货退款");
    }
}
