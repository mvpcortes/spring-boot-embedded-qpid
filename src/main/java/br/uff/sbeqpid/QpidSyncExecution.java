package br.uff.sbeqpid;

import java.util.function.Supplier;

/**
 * This interface is a facility to get the method fotr sync and not get all broker manager.
 */
public interface QpidSyncExecution {

    <T>  T syncExcecution(Supplier<T> func);
}
