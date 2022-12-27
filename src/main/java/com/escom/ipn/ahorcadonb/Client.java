package com.escom.ipn.ahorcadonb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            // Abrimos el selector y el canal del cliente
            Selector selector = Selector.open();
            SocketChannel client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress("localhost", 8080));
            client.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

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
                    // Si el key es de conexión, enviamos el mensaje al servidor
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            continue;
                        }

                    }
                    // Si el key es de lectura, leemos la respuesta del servidor
                    else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int read = channel.read(buffer);
                        if (read > 0) {
                            buffer.flip();
                            String word = new String(buffer.array(), 0, read);
                            //System.out.println("Mensaje recibido del servidor: " + word);


                            char[] hidden = new char[word.length()];
                            
                            int attemps = word.length();

                            for (int i = 0; i < word.length(); i++) {
                                hidden[i] = '_';
                            }

                            boolean found;
                            int charFound = 0;

                            while (attemps > 0 && charFound != word.length()) {
                                found = false;

                                System.out.println("\nAdivina la palabra: ");
                                System.out.println("Número de vidas: " + String.valueOf(attemps));
                                System.out.println(Arrays.toString(hidden) + "\n");
                                System.out.print("Ingresa una letra: ");
                                char letra = sc.next().charAt(0);

                                for (int i = 0; i < hidden.length; i++) {
                                    if (letra == word.charAt(i)) {
                                        hidden[i] = letra;
                                        found = true;
                                        charFound += 1;
                                    }
                                }

                                if (!found) attemps -= 1;

                            }

                            if (charFound == word.length()) {
                                System.out.println("\n!Felicidades!");
                                System.out.println(Arrays.toString(hidden) + "\n");
                            } else {
                                System.out.println("Suerte para la próxima ):");
                            }
                            System.exit(0);
                        }
                    }
                    iterator.remove();

                }

            }
        } catch (IOException e) {
        }
    }
}