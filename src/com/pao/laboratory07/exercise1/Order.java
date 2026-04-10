package com.pao.laboratory07.exercise1;

import com.pao.laboratory07.exercise1.exceptions.CannotCancelFinalOrderException;
import com.pao.laboratory07.exercise1.exceptions.CannotRevertInitialOrderStateException;
import com.pao.laboratory07.exercise1.exceptions.OrderIsAlreadyFinalException;

import java.util.ArrayDeque;
import java.util.Deque;

public class Order {
    private OrderState currentState;
    private final Deque<OrderState> history; // stack of previous states

    public Order(OrderState initialState) {
        this.currentState = initialState;
        this.history = new ArrayDeque<>();
    }

    public OrderState getCurrentState() {
        return currentState;
    }

    /**
     * Advances the order to the next state in the chain:
     * PLACED -> PROCESSED -> SHIPPED -> DELIVERED
     * Throws if the order is already in a final state.
     */
    public void nextState() throws OrderIsAlreadyFinalException {
        if (currentState.isFinal()) {
            throw new OrderIsAlreadyFinalException();
        }
        history.push(currentState);
        currentState = currentState.next();
    }

    /**
     * Cancels the order from any non-final state.
     * Throws if the order is already in a final state.
     */
    public void cancel() throws CannotCancelFinalOrderException {
        if (currentState.isFinal()) {
            throw new CannotCancelFinalOrderException();
        }
        history.push(currentState);
        currentState = OrderState.CANCELED;
    }

    /**
     * Reverts to the previous state (pops from history stack).
     * Works even from final states.
     * Throws if there is no previous state.
     */
    public void undoState() throws CannotRevertInitialOrderStateException {
        if (history.isEmpty()) {
            throw new CannotRevertInitialOrderStateException();
        }
        currentState = history.pop();
    }
}