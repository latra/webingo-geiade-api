Feature: Leave Game
  In order to remove a player to a game
  As a player
  I want to leave a game

  Scenario: Leave a game as a player
    Given I login as "player1" with password "password"
    Given Player "player1@webingo.org" is in game "game" and playing with card 1
    When I leave a game when I'm playing with a Card
    Then The response code is 204
    And It has been removed the game in the player with card 1



  Scenario: Leave an game as a player
    Given I login as "player1" with password "password"
    Given There is card 1 in the game "game" without a player
    When I leave a game when I'm not playing
    Then The response code is 409
    And The system throws an error