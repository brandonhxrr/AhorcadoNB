package com.escom.ipn.ahorcadonb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) {
        try {
            // Abrimos el selector y el canal del servidor
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(8080));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Servidor listo...");

            while (true) {
                // Esperamos a que haya algún evento de entrada o salida
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                // Obtenemos los keys de los canales listos para ser procesados
                Set < SelectionKey > keys = selector.selectedKeys();
                Iterator < SelectionKey > iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // Si el key es de aceptación de conexión, aceptamos la conexión
                    // y registramos el canal del cliente para escribir
                    if (key.isAcceptable()) {
                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE);
                        // Enviamos el mensaje al cliente
                        
                        String[] words = {"animal", "perro", "gato", "vaca", "cerdo", "pecho", "cintura", "cadera"};
              
                        int x = (int)(Math.random()*(words.length)+1);
              
                        String message = words[x];
                        System.out.println("Enviando palabra: " + words[x]);
                        
                        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                        client.write(buffer);
                        buffer.clear();
                    }
                    // Si el key es de escritura y el canal del cliente está listo para escribir,
                    // no hacemos nada, ya que hemos enviado el mensaje al cliente cuando se aceptó la conexión
                    else if (key.isWritable()) {
                        // No hacemos nada
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}