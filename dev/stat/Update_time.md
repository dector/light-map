Generation code
===============

(v.1)
-----

	{
		int w = 1000;
		int h = 1000;

		map = new LightMap(w, h);

		Random rnd = new Random();

		for (int i = 0; i < 1000; i++) {
			map.addStaticLight(new Light(rnd.nextInt(10)), rnd.nextInt(w), rnd.nextInt(h));
		}

		playerX = 10;
		playerY = 10;
		dynamicLightId = map.addDynamicLight(new Light(3), Position.from(playerX, playerY));
	}

Tests
=====

#eca941f067c23832f0af66edacf1b88de94b69cb (10 May 2013)
-------------------------------------------------------

Gen. code: v.1

  - 1K static lights => 0.071 s;
  - 10K static lights => 0.343 s;
  - 100K static lights => 2.721 s;
  - 1M static lights => 17.556 s.

