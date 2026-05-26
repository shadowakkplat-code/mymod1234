package com.example.mymod;

public class RightHandConfig {
    // Координаты ПРАВОЙ руки
    public static float rightX = 0.12f; 
    public static float rightY = 0.10f;
    public static float rightZ = -0.45f;

    // Координаты ЛЕВОЙ руки
    public static float leftX = -0.315f; 
    public static float leftY = 0.10f;
    public static float leftZ = -0.45f;
    
    // Режим анимации удара: 0 - Ванилла, 1 - Прокрут 360
    public static int swingMode = 0; 

    // ID выбранной одиночной частицы из сетки (от 0 до 39)
    public static int activeParticleId = 0;
}
