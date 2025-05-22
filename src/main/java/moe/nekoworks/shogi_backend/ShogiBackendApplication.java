package moe.nekoworks.shogi_backend;

import moe.nekoworks.shogi_backend.shogi.Board;
import moe.nekoworks.shogi_backend.shogi.Square;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShogiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShogiBackendApplication.class, args);
	}

}
