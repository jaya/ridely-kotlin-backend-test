/* Agora podemos exigir NOT NULL e criar o índice espacial */
ALTER TABLE driver
    MODIFY COLUMN location POINT NOT NULL;