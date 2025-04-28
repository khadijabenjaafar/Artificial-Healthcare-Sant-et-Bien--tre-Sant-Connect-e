package org.example.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {

    // Remplace cette clé par ta clé secrète Stripe
    private static final String STRIPE_API_KEY = "sk_test_51RGjEt03F1u89IBP849cjWDLt6HKQGJOsH6qhoY6MMoVY2r6SqzyBZTNfQqkLnhfgdnp1XbY6ZQIgHREN95b0X4t00tKdJtYlc";

    static {
        // Configure Stripe avec la clé API secrète
        Stripe.apiKey = STRIPE_API_KEY;
    }

    // Méthode pour créer un PaymentIntent
    public PaymentIntent createPaymentIntent(long amount) throws StripeException {
        // Créer les paramètres pour la création du PaymentIntent
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount) // Montant en centimes (ex : 50 USD = 5000)
                        .setCurrency("usd") // Devise (ici USD, tu peux modifier)
                        .build();

        // Créer et retourner le PaymentIntent
        return PaymentIntent.create(params);
    }
}
