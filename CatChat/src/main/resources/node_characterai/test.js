const CharacterAI = require('node_characterai');
const characterAI = new CharacterAI();

(async() => {
    await characterAI.authenticateAsGuest();

    const characterId = "7A4sm3pOCTWcO4ZLFRKiGw3-s65oLwRxFbPQcksazDM" // Discord moderator

    const chat = await characterAI.createOrContinueChat(characterId);

    let response = await chat.sendAndAwaitResponse('Hello', true);

    console.log(response);
    // use response.text to use it in a string.


})();