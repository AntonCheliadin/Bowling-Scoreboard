package task.bowling

class Game {

    static hasMany = [frames: Frame]

    static constraints = {
        frames(maxSize: 10)
    }

    @Override
    public String toString() {
        return "Game #" + id
    }
}
