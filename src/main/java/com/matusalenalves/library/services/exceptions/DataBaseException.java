package com.matusalenalves.library.services.exceptions;

import java.io.Serial;

/**
 * Lançada quando uma operação no banco falha por violação de integridade
 * referencial (ex.: exclusão bloqueada por uma foreign key) não coberta por
 * uma verificação explícita de regra de negócio.
 * <p>
 * Funciona como rede de segurança contra condições de corrida: mesmo que o
 * {@code Service} já tenha checado a regra de negócio (ex.: RN05, RN06)
 * antes de excluir, outro vínculo pode ter sido criado entre a checagem e a
 * exclusão. Deve ser traduzida pela camada {@code controller} em uma
 * resposta HTTP 409 Conflict.
 */
public class DataBaseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DataBaseException(String message) {
        super(message);
    }
}