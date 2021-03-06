package cat.udl.eps.entsoftarch.webingogeiadeapi.handler;


import cat.udl.eps.entsoftarch.webingogeiadeapi.domain.Game;
import cat.udl.eps.entsoftarch.webingogeiadeapi.handler.exception.GameException;
import cat.udl.eps.entsoftarch.webingogeiadeapi.repository.GameRepository;
import cat.udl.eps.entsoftarch.webingogeiadeapi.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import cat.udl.eps.entsoftarch.webingogeiadeapi.domain.Card;
import cat.udl.eps.entsoftarch.webingogeiadeapi.domain.Player;
import cat.udl.eps.entsoftarch.webingogeiadeapi.handler.exceptions.ShowResultException;
import cat.udl.eps.entsoftarch.webingogeiadeapi.repository.CardRepository;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;


@Component
@RepositoryEventHandler
public class GameEventHandler {
    final Logger logger = LoggerFactory.getLogger(Game.class);

    @Autowired
    CardRepository cardRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @HandleBeforeCreate
    public void handleGamerPreCreate(Game game) {
        logger.info("Before creating: {}", game.toString());
    }
    @HandleAfterCreate
    public void handleGamerAfterCreate(Game game) {
        logger.info("After creating: {}", game.toString());
        game.setPrice(3);
        game.setBingoPrize(0);
        game.setLinePrize(0);
        game.setFinished(false);
        game.setBingoWinner(null);
        game.setLineWinner(null);
    }

    @HandleBeforeSave
    public void handleGamePreSave(Game game){
        logger.info("Before updating: {}", game.toString());
    }

    @HandleBeforeDelete
    public void handleGamePreDelete(Game game){
        logger.info("Before deleting: {}", game.toString());
    }

    @HandleBeforeLinkSave
    public void handleGamePreLinkSave(Game game, Object o) {
        logger.info("Before linking: {} to {}", game.toString(), o.toString());
    }

    @HandleAfterCreate
    public void handleGamePostCreate(Game game){
        if (game.isFinished() || game.getBingoWinner() != null || game.getLineWinner() != null)
            throw new GameException("There was an error while creating the game");
    }

    @HandleAfterDelete
    public void handleGamePostDelete(Game game){
        logger.info("After deleting: {}", game.toString());
    }

    @HandleAfterLinkSave
    public void handleGamePostLinkSave(Game game, Object o) {
        logger.info("After linking: {} to {}", game.toString(), o.toString());
    }

    @HandleAfterSave
    public void handleGamePostSave(Game game) throws ShowResultException{

        if (game.getBingoWinner()!=null){
            Boolean real = true;
            Player p = game.getBingoWinner();
            Card c = cardRepository.findByPlayer(p);
            if (c==null){
                throw new ShowResultException("The player does not have any card!");
            }
            int [] gamesN = game.getNums();

            if (gamesN == null){
                throw new ShowResultException("The game does not have any number yet! It is not posible to sing Bingo");
            }
            int [][] playerN = c.getNums();

            for (int i=0; i<3; i++) {
                if (!numerosDeLaLineaEstanDits(playerN[i],gamesN)){
                    real = false;
                }
            }
            if (!real){
                throw new ShowResultException("The player does not have all the bingo numbers!!");
            }
        }

        else if(game.getLineWinner()!=null){
            Boolean real = false;
            Player p = game.getLineWinner();
            Card c = cardRepository.findByPlayer(p);
            if (c==null){
                throw new ShowResultException("The player does not have any card!");
            }
            int [] gamesN = game.getNums();

            if (gamesN == null){
                throw new ShowResultException("The game does not have any number yet! It is not posible to sing line");
            }
            int [][] playerN = c.getNums();

            for (int i=0; i<3; i++) {
                if (numerosDeLaLineaEstanDits(playerN[i],gamesN)){
                    real = true;
                }
            }
            if (!real){
                throw new ShowResultException("The player does not have all the line numbers!!");
            }
            
        }

        if (game.isFinished()) {
            if ((game.getLineWinner() == null) || (game.getBingoWinner() == null) ) {
                throw new GameException("Game has finished without a line or bingo winner");
            }
            if ((game.getLinePrize() < 0.0) || (game.getBingoPrize() < 0.0 )) {
                throw new GameException("Game has finished without a good saved line/bingo prize");
            }
        }

    }

    public boolean numerosDeLaLineaEstanDits(int [] playerN, int [] gameN){
        for (int j = 0; j < playerN.length; j++) {
            Boolean trobat = false;
            for (int z = 0; z <gameN.length;z++){
                if (playerN[j] == gameN[z]){
                    trobat = true;
                }
            }
            if (trobat == false){
                return false;
            }

        }
        return true;
    }

}
