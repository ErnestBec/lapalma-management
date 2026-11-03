-- La Palma / Jarciería — PostgreSQL MVP v0.1
-- Target PostgreSQL 16+; intended for Flyway V1 migration.
-- Review before deploying: SQL not executed against production.
CREATE SCHEMA IF NOT EXISTS lapalma;
SET search_path TO lapalma, public;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE role (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), code varchar(24) NOT NULL UNIQUE,
 name varchar(80) NOT NULL, CHECK (code IN ('ADMIN','SELLER'))
);
CREATE TABLE app_user (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), role_id uuid NOT NULL REFERENCES role(id),
 username varchar(80) NOT NULL UNIQUE, password_hash text NOT NULL,
 full_name varchar(160) NOT NULL, active boolean NOT NULL DEFAULT true,
 created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE audit_event (
 id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, actor_id uuid REFERENCES app_user(id),
 entity_type varchar(64) NOT NULL, entity_id varchar(64) NOT NULL,
 action varchar(64) NOT NULL, reason text,
 old_data jsonb, new_data jsonb, occurred_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE category (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), name varchar(120) NOT NULL,
 active boolean NOT NULL DEFAULT true
);
CREATE TABLE unit (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), code varchar(20) NOT NULL UNIQUE,
 name varchar(80) NOT NULL, dimension varchar(20) NOT NULL,
 factor_to_dimension_base numeric(20,8) NOT NULL CHECK (factor_to_dimension_base > 0),
 CHECK (dimension IN ('COUNT','VOLUME'))
);
CREATE TABLE product (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), category_id uuid REFERENCES category(id),
 base_unit_id uuid NOT NULL REFERENCES unit(id), sku varchar(80) NOT NULL UNIQUE,
 name varchar(180) NOT NULL, stock_min numeric(20,6) NOT NULL DEFAULT 0 CHECK(stock_min >= 0),
 active boolean NOT NULL DEFAULT true,
 created_at timestamptz NOT NULL DEFAULT now(), updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE product_presentation (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), product_id uuid NOT NULL REFERENCES product(id),
 unit_id uuid NOT NULL REFERENCES unit(id), label varchar(120) NOT NULL,
 factor_to_base numeric(20,8) NOT NULL CHECK(factor_to_base > 0),
 active boolean NOT NULL DEFAULT true, UNIQUE(id,product_id)
);
CREATE TABLE product_barcode (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), presentation_id uuid NOT NULL REFERENCES product_presentation(id),
 code varchar(128) NOT NULL UNIQUE, origin varchar(16) NOT NULL,
 symbology varchar(24) NOT NULL DEFAULT 'CODE128', active boolean NOT NULL DEFAULT true,
 CHECK (origin IN ('INTERNAL','MANUFACTURER'))
);
CREATE TABLE product_price_history (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), product_id uuid NOT NULL REFERENCES product(id),
 presentation_id uuid REFERENCES product_presentation(id), price numeric(18,2) NOT NULL CHECK(price >= 0),
 effective_at timestamptz NOT NULL DEFAULT now(), changed_by uuid NOT NULL REFERENCES app_user(id)
);
CREATE TABLE supplier (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), name varchar(180) NOT NULL,
 contact_info jsonb NOT NULL DEFAULT '{}'::jsonb, active boolean NOT NULL DEFAULT true
);
CREATE TABLE product_supplier (
 product_id uuid NOT NULL REFERENCES product(id), supplier_id uuid NOT NULL REFERENCES supplier(id),
 preferred boolean NOT NULL DEFAULT false, active boolean NOT NULL DEFAULT true,
 PRIMARY KEY(product_id,supplier_id)
);
CREATE TABLE customer (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), full_name varchar(160),
 contact_info jsonb, consent_at timestamptz, active boolean NOT NULL DEFAULT true
);

CREATE TABLE purchase (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), supplier_id uuid NOT NULL REFERENCES supplier(id),
 created_by uuid NOT NULL REFERENCES app_user(id), status varchar(20) NOT NULL DEFAULT 'DRAFT',
 currency char(3) NOT NULL DEFAULT 'MXN', invoice_total numeric(18,2) NOT NULL DEFAULT 0 CHECK(invoice_total >= 0),
 invoice_reference varchar(120), created_at timestamptz NOT NULL DEFAULT now(),
 CHECK (currency='MXN'), CHECK (status IN ('DRAFT','ORDERED','PARTIALLY_RECEIVED','RECEIVED','CANCELLED'))
);
CREATE TABLE purchase_line (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), purchase_id uuid NOT NULL REFERENCES purchase(id),
 product_id uuid NOT NULL REFERENCES product(id), quantity_base numeric(20,6) NOT NULL CHECK(quantity_base > 0),
 invoiced_unit_cost numeric(18,6) NOT NULL CHECK(invoiced_unit_cost >= 0),
 invoiced_total numeric(18,2) NOT NULL CHECK(invoiced_total >= 0),
 UNIQUE(id,purchase_id)
);
CREATE TABLE goods_receipt (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), purchase_id uuid NOT NULL REFERENCES purchase(id),
 received_by uuid NOT NULL REFERENCES app_user(id), approved_by uuid REFERENCES app_user(id),
 received_at timestamptz NOT NULL DEFAULT now(), state varchar(20) NOT NULL DEFAULT 'DRAFT',
 CHECK(state IN ('DRAFT','POSTED','CANCELLED')), UNIQUE(id,purchase_id)
);
CREATE TABLE goods_receipt_line (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), receipt_id uuid NOT NULL,
 purchase_id uuid NOT NULL, purchase_line_id uuid NOT NULL,
 invoiced_quantity_reference numeric(20,6) NOT NULL CHECK(invoiced_quantity_reference >= 0),
 counted_quantity numeric(20,6) NOT NULL CHECK(counted_quantity >= 0),
 posted_quantity numeric(20,6) NOT NULL CHECK(posted_quantity >= 0),
 selected_policy varchar(20) NOT NULL, reason text,
 FOREIGN KEY(receipt_id,purchase_id) REFERENCES goods_receipt(id,purchase_id),
 FOREIGN KEY(purchase_line_id,purchase_id) REFERENCES purchase_line(id,purchase_id),
 CHECK(selected_policy IN ('PHYSICAL','INVOICED'))
);
CREATE TABLE product_lot (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), product_id uuid NOT NULL REFERENCES product(id),
 lot_code varchar(100) NOT NULL, expires_on date, UNIQUE(product_id,lot_code)
);
CREATE TABLE stock_position (
 product_id uuid PRIMARY KEY REFERENCES product(id), recorded_quantity numeric(20,6) NOT NULL DEFAULT 0,
 unavailable_quantity numeric(20,6) NOT NULL DEFAULT 0 CHECK(unavailable_quantity >= 0),
 version bigint NOT NULL DEFAULT 0, updated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE inventory_count (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), performed_by uuid NOT NULL REFERENCES app_user(id),
 approved_by uuid REFERENCES app_user(id), status varchar(20) NOT NULL DEFAULT 'DRAFT',
 counted_at timestamptz NOT NULL DEFAULT now(), CHECK(status IN ('DRAFT','SUBMITTED','APPROVED','REJECTED'))
);
CREATE TABLE inventory_count_line (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), count_id uuid NOT NULL REFERENCES inventory_count(id),
 product_id uuid NOT NULL REFERENCES product(id), theoretical_quantity numeric(20,6) NOT NULL,
 physical_quantity numeric(20,6) NOT NULL CHECK(physical_quantity >= 0), UNIQUE(count_id,product_id)
);
CREATE TABLE promotion (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), name varchar(140) NOT NULL,
 type varchar(20) NOT NULL, benefit_value numeric(18,6), required_quantity numeric(20,6),
 charged_quantity numeric(20,6), starts_at timestamptz NOT NULL, ends_at timestamptz NOT NULL,
 priority integer NOT NULL, active boolean NOT NULL DEFAULT false,
 created_by uuid NOT NULL REFERENCES app_user(id),
 CHECK (type IN ('PERCENT','AMOUNT','SPECIAL_PRICE','QUANTITY')),
 CHECK(ends_at > starts_at), CHECK(benefit_value IS NULL OR benefit_value >= 0),
 CHECK(required_quantity IS NULL OR required_quantity > 0),
 CHECK(charged_quantity IS NULL OR charged_quantity >= 0),
 CHECK(type <> 'QUANTITY' OR (required_quantity IS NOT NULL AND charged_quantity IS NOT NULL AND charged_quantity < required_quantity)),
 CHECK(type = 'QUANTITY' OR (benefit_value IS NOT NULL)),
 CHECK(type <> 'PERCENT' OR benefit_value <= 100)
);
CREATE TABLE promotion_product (
 promotion_id uuid NOT NULL REFERENCES promotion(id), product_id uuid NOT NULL REFERENCES product(id),
 PRIMARY KEY(promotion_id,product_id)
);
CREATE TABLE cash_shift (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), opened_by uuid NOT NULL REFERENCES app_user(id),
 closed_by uuid REFERENCES app_user(id), opened_at timestamptz NOT NULL DEFAULT now(),
 closed_at timestamptz, opening_cash numeric(18,2) NOT NULL DEFAULT 0 CHECK(opening_cash >= 0),
 expected_cash numeric(18,2), counted_cash numeric(18,2), difference numeric(18,2),
 status varchar(16) NOT NULL DEFAULT 'OPEN', CHECK(status IN ('OPEN','CLOSING_REVIEW','CLOSED'))
);
CREATE TABLE cash_shift_user (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), shift_id uuid NOT NULL REFERENCES cash_shift(id),
 user_id uuid NOT NULL REFERENCES app_user(id), joined_at timestamptz NOT NULL DEFAULT now(), left_at timestamptz,
 CHECK(left_at IS NULL OR left_at >= joined_at)
);
CREATE TABLE sale (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), seller_id uuid NOT NULL REFERENCES app_user(id),
 customer_id uuid REFERENCES customer(id), shift_id uuid REFERENCES cash_shift(id),
 status varchar(20) NOT NULL DEFAULT 'DRAFT', currency char(3) NOT NULL DEFAULT 'MXN',
 gross_total numeric(18,2) NOT NULL DEFAULT 0 CHECK(gross_total >= 0),
 discount_total numeric(18,2) NOT NULL DEFAULT 0 CHECK(discount_total >= 0),
 net_total numeric(18,2) NOT NULL DEFAULT 0 CHECK(net_total >= 0),
 created_at timestamptz NOT NULL DEFAULT now(), confirmed_at timestamptz,
 CHECK(currency='MXN'), CHECK(status IN ('DRAFT','CONFIRMED','CANCELLED')),
 CHECK(discount_total <= gross_total), CHECK(net_total = gross_total-discount_total)
);
CREATE TABLE sale_line (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), sale_id uuid NOT NULL REFERENCES sale(id),
 product_id uuid NOT NULL REFERENCES product(id), presentation_id uuid REFERENCES product_presentation(id),
 quantity_base numeric(20,6) NOT NULL CHECK(quantity_base > 0),
 display_quantity numeric(20,6) NOT NULL CHECK(display_quantity > 0),
 unit_price_snapshot numeric(18,6) NOT NULL CHECK(unit_price_snapshot >= 0),
 promotion_id uuid REFERENCES promotion(id), promotion_snapshot jsonb,
 groups_applied integer NOT NULL DEFAULT 0 CHECK(groups_applied >= 0),
 remainder_quantity numeric(20,6) NOT NULL DEFAULT 0 CHECK(remainder_quantity >= 0),
 gross_total numeric(18,2) NOT NULL CHECK(gross_total >= 0),
 discount_amount numeric(18,2) NOT NULL DEFAULT 0 CHECK(discount_amount >= 0),
 net_total numeric(18,2) NOT NULL CHECK(net_total >= 0),
 CHECK(net_total = gross_total-discount_amount), CHECK(discount_amount <= gross_total)
);
CREATE TABLE payment (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), sale_id uuid NOT NULL REFERENCES sale(id),
 shift_id uuid NOT NULL REFERENCES cash_shift(id), received_by uuid NOT NULL REFERENCES app_user(id),
 method varchar(16) NOT NULL, amount numeric(18,2) NOT NULL CHECK(amount > 0),
 bank_reference varchar(160), evidence_confirmed boolean NOT NULL DEFAULT false,
 paid_at timestamptz NOT NULL DEFAULT now(), CHECK(method IN ('CASH','TRANSFER')),
 CHECK(method <> 'TRANSFER' OR (bank_reference IS NOT NULL AND length(trim(bank_reference))>0 AND evidence_confirmed))
);
CREATE TABLE receipt_document (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), sale_id uuid NOT NULL REFERENCES sale(id),
 document_type varchar(16) NOT NULL CHECK(document_type IN ('PDF','THERMAL')),
 document_number varchar(80) NOT NULL UNIQUE, uri text, generated_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE sale_cancellation (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), sale_id uuid NOT NULL UNIQUE REFERENCES sale(id),
 actor_id uuid NOT NULL REFERENCES app_user(id), authorized_by uuid REFERENCES app_user(id),
 reason text NOT NULL CHECK(length(trim(reason)) > 0), cancelled_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE sales_return (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), sale_id uuid NOT NULL REFERENCES sale(id),
 initiated_by uuid NOT NULL REFERENCES app_user(id), status varchar(20) NOT NULL DEFAULT 'REQUESTED',
 reason text NOT NULL, requested_at timestamptz NOT NULL DEFAULT now(), confirmed_at timestamptz,
 recalculated_total numeric(18,2) CHECK(recalculated_total >= 0),
 CHECK(status IN ('REQUESTED','PENDING_APPROVAL','APPROVED','REJECTED','COMPLETED')),
 UNIQUE(id,sale_id)
);
CREATE TABLE return_line (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), return_id uuid NOT NULL,
 sale_id uuid NOT NULL, sale_line_id uuid NOT NULL, returned_quantity numeric(20,6) NOT NULL CHECK(returned_quantity > 0),
 condition varchar(20) NOT NULL CHECK(condition IN ('SEALED','UNUSED','OPENED','USED','DAMAGED')),
 saleable_quantity numeric(20,6) NOT NULL DEFAULT 0 CHECK(saleable_quantity >= 0),
 retained_quantity numeric(20,6) NOT NULL CHECK(retained_quantity >= 0),
 promotion_recalc_snapshot jsonb, refund_allocation numeric(18,2) NOT NULL DEFAULT 0 CHECK(refund_allocation >= 0),
 FOREIGN KEY(return_id,sale_id) REFERENCES sales_return(id,sale_id),
 FOREIGN KEY(sale_line_id) REFERENCES sale_line(id),
 CHECK(saleable_quantity <= returned_quantity)
);
CREATE TABLE return_authorization (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), return_id uuid NOT NULL REFERENCES sales_return(id),
 authorized_by uuid NOT NULL REFERENCES app_user(id), decision varchar(16) NOT NULL,
 handling varchar(20) NOT NULL, reason text NOT NULL,
 waived_amount numeric(18,2) NOT NULL DEFAULT 0 CHECK(waived_amount >= 0),
 decided_at timestamptz NOT NULL DEFAULT now(),
 CHECK(decision IN ('APPROVED','REJECTED')),
 CHECK(handling IN ('CHARGE_DIFFERENCE','WAIVE_DIFFERENCE','REJECT_RETURN','OPENED_PRODUCT','OUTSIDE_POLICY'))
);
CREATE TABLE refund (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), return_id uuid NOT NULL REFERENCES sales_return(id),
 original_payment_id uuid REFERENCES payment(id), shift_id uuid REFERENCES cash_shift(id),
 method varchar(16) NOT NULL CHECK(method IN ('CASH','TRANSFER')),
 amount numeric(18,2) NOT NULL CHECK(amount > 0),
 processed_by uuid NOT NULL REFERENCES app_user(id), processed_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE cash_movement (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), shift_id uuid NOT NULL REFERENCES cash_shift(id),
 payment_id uuid UNIQUE REFERENCES payment(id), refund_id uuid UNIQUE REFERENCES refund(id),
 movement_type varchar(20) NOT NULL, amount_signed numeric(18,2) NOT NULL CHECK(amount_signed <> 0),
 reason text, actor_id uuid NOT NULL REFERENCES app_user(id), occurred_at timestamptz NOT NULL DEFAULT now(),
 CHECK(movement_type IN ('SALE_CASH','CHANGE','EXPENSE','WITHDRAWAL','REFUND_CASH','OPENING_ADJUSTMENT','CORRECTION')),
 CHECK(NOT (payment_id IS NOT NULL AND refund_id IS NOT NULL))
);
CREATE TABLE cash_close_review (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), shift_id uuid NOT NULL REFERENCES cash_shift(id),
 approved_by uuid NOT NULL REFERENCES app_user(id), decision varchar(20) NOT NULL,
 difference numeric(18,2) NOT NULL, reason text NOT NULL,
 reviewed_at timestamptz NOT NULL DEFAULT now(), CHECK(decision IN ('APPROVED','REJECTED'))
);
CREATE TABLE inventory_discrepancy (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), product_id uuid NOT NULL REFERENCES product(id),
 receipt_line_id uuid REFERENCES goods_receipt_line(id), sale_id uuid REFERENCES sale(id),
 count_line_id uuid REFERENCES inventory_count_line(id),
 theoretical_quantity numeric(20,6), physical_quantity numeric(20,6),
 delta_quantity numeric(20,6) NOT NULL, reason text NOT NULL,
 status varchar(16) NOT NULL DEFAULT 'PENDING',
 recorded_by uuid NOT NULL REFERENCES app_user(id), created_at timestamptz NOT NULL DEFAULT now(),
 CHECK(status IN ('PENDING','RECONCILED')),
 CHECK(num_nonnulls(receipt_line_id,sale_id,count_line_id) = 1)
);
CREATE TABLE inventory_reconciliation (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), discrepancy_id uuid NOT NULL REFERENCES inventory_discrepancy(id),
 authorized_by uuid NOT NULL REFERENCES app_user(id), resolution_kind varchar(20) NOT NULL,
 notes text NOT NULL, resolved_at timestamptz NOT NULL DEFAULT now(),
 CHECK(resolution_kind IN ('RECEIPT_CORRECTION','ADJUSTMENT','NO_MOVEMENT'))
);
CREATE TABLE inventory_movement (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), product_id uuid NOT NULL REFERENCES product(id),
 lot_id uuid REFERENCES product_lot(id), signed_quantity numeric(20,6) NOT NULL CHECK(signed_quantity <> 0),
 movement_type varchar(20) NOT NULL, goods_receipt_line_id uuid REFERENCES goods_receipt_line(id),
 sale_line_id uuid REFERENCES sale_line(id), return_line_id uuid REFERENCES return_line(id),
 reconciliation_id uuid REFERENCES inventory_reconciliation(id),
 count_line_id uuid REFERENCES inventory_count_line(id),
 actor_id uuid NOT NULL REFERENCES app_user(id), idempotency_key varchar(160) NOT NULL UNIQUE,
 occurred_at timestamptz NOT NULL DEFAULT now(),
 CHECK(movement_type IN ('RECEIPT','SALE','RETURN','ADJUSTMENT','WASTE')),
 CHECK(num_nonnulls(goods_receipt_line_id,sale_line_id,return_line_id,reconciliation_id,count_line_id) = 1)
);
CREATE TABLE backup_execution (
 id uuid PRIMARY KEY DEFAULT gen_random_uuid(), started_at timestamptz NOT NULL DEFAULT now(),
 completed_at timestamptz, status varchar(20) NOT NULL,
 destination text, result_summary text, CHECK(status IN ('RUNNING','SUCCESS','FAILED'))
);
-- Seed approved roles, optional units require domain review before insertion.
INSERT INTO role(code,name) VALUES ('ADMIN','Administrador'),('SELLER','Vendedor');
