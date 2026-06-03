package wms.purchase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseOrder {
    private final String orderNo;
    private String product;
    private int quantity;
    private String supplier;
    private String inboundOrderNo;
    private String invoiceNo;
    private String returnOrderNo;
    private PurchaseOrderState state;
    private final List<OperationLog> logs = new ArrayList<>();

    public PurchaseOrder(String orderNo, String product, int quantity, String supplier) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new PurchaseOrderException("采购单号不能为空");
        }
        if (product == null || product.trim().isEmpty()) {
            throw new PurchaseOrderException("商品名称不能为空");
        }
        if (quantity <= 0) {
            throw new PurchaseOrderException("采购数量必须大于0");
        }
        if (supplier == null || supplier.trim().isEmpty()) {
            throw new PurchaseOrderException("供应商不能为空");
        }
        this.orderNo = orderNo;
        this.product = product;
        this.quantity = quantity;
        this.supplier = supplier;
        this.state = new DraftState();
        record("创建采购单", "-", state.getName());
    }

    public void edit(String product, int quantity, String supplier) {
        state.edit(this, product, quantity, supplier);
    }

    public void approve() {
        state.approve(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public void generateInboundOrder() {
        state.generateInboundOrder(this);
    }

    public void generateInvoice() {
        state.generateInvoice(this);
    }

    public void pay() {
        state.pay(this);
    }

    public void returnAndRefund() {
        state.returnAndRefund(this);
    }

    void updateDetails(String product, int quantity, String supplier) {
        if (product == null || product.trim().isEmpty()) {
            throw new PurchaseOrderException("商品名称不能为空");
        }
        if (quantity <= 0) {
            throw new PurchaseOrderException("操作失败：采购数量必须大于 0。");
        }
        if (supplier == null || supplier.trim().isEmpty()) {
            throw new PurchaseOrderException("供应商不能为空");
        }
        this.product = product;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    void changeState(PurchaseOrderState newState, String operation) {
        String oldState = state.getName();
        state = newState;
        record(operation, oldState, newState.getName());
    }

    void record(String operation, String fromState, String toState) {
        logs.add(new OperationLog(operation, fromState, toState));
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getStateName() {
        return state.getName();
    }

    public String getInboundOrderNo() {
        return inboundOrderNo;
    }

    void setInboundOrderNo(String inboundOrderNo) {
        this.inboundOrderNo = inboundOrderNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getReturnOrderNo() {
        return returnOrderNo;
    }

    void setReturnOrderNo(String returnOrderNo) {
        this.returnOrderNo = returnOrderNo;
    }

    public List<OperationLog> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "orderNo='" + orderNo + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", supplier='" + supplier + '\'' +
                ", state='" + state.getName() + '\'' +
                '}';
    }
}
