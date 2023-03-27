package com.company.bi.events.payloads

/**
 * Payment Event JSON related representation.
 */
case class PaymentEventPayload(paymentStep: Option[String], transactionId: Option[String], status: Option[String], amount: Option[String], items: Option[String],
                               email: Option[String], opt_out: Option[String], details: Option[String], userId: Option[String], merchantType: Option[String])

