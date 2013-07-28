Light Map Lib
=============

![Hello from lightmap][screen-0]

![Lightmap in near view][screen-1]

![A lot of lights][screen-2]

[screen-0]: https://lh5.googleusercontent.com/-caSd2dv4q2A/UfVaKKWeRSI/AAAAAAAADQQ/2STdFKSUG-4/w816-h638-no/lightmap-0.png "Hello from lightmap"
[screen-1]: https://lh5.googleusercontent.com/-1IcSBGbt-nY/UfVZwY11tAI/AAAAAAAADOo/p2LGNmA0R8I/w816-h638-no/lightmap-1.png "Lightmap in near view"
[screen-2]: https://lh4.googleusercontent.com/-NAOywfyqoeg/UfVZ1p5zizI/AAAAAAAADOw/aKLbSjI5iok/w816-h638-no/lightmap-2.png "A lot of lights"

This is simple and open Java library.
Main aim is to implement light math for tile maps.

Components
==========

  - Static lights.
  - Dynamic lights.

Usage
==========

<code>

    int w = 25;
    int h = 25;

    map = new LightMap(w, h);
    map.addStaticLight(new Light(3), 5, 5);
    map.addStaticLight(new Light(4), 9, 5);
    map.addStaticLight(new Light(6), 16, 8);
    map.addStaticLight(new Light(5), 6, 15);

    map.addStaticLight(new Light(2, 5), 20, 18);
    map.addStaticLight(Light.lightCircle(3), 5, 20);
    map.addStaticLight(Light.lightSquare(3), 13, 20);

    playerPos.set(10, 10);
    dynamicLightId = map.addDynamicLight(new Light(3), new Position(playerPos));

</code>