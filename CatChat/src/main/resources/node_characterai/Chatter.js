const CharacterAI = require('node_characterai');
const characterAI = new CharacterAI();

class Chatter {
    chat;

    async Chatter() {
        await characterAI.authenticateAsGuest();
        const characterId = "7A4sm3pOCTWcO4ZLFRKiGw3-s65oLwRxFbPQcksazDM" // Discord moderator
        this.chat = await characterAI.createOrContinueChat(characterId);

        let response = await this.chat.sendAndAwaitResponse('Hello!', true);
        console.log(response);
    }

    async chatSingleMessage(message) {
        if (typeof (message) == 'string') {
            return await this.chat.sendAndAwaitResponse(message, true);
        }
    }
}
