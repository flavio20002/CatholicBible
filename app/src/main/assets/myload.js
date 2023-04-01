function handlebookmarkClick() {
    if (this.className == "bookmark") {
        this.className = "";
        app.rimuoviSegnalibro(this.innerHTML);
    } else {
        this.className = "bookmark";
        app.aggiungiSegnalibro(this.innerHTML);
    }
}

window.onload = function () {
    var anchors = document.getElementsByTagName('sup');
    for (var i = 0; i < anchors.length; i++) {
        var anchor = anchors[i];
        anchor.onclick = handlebookmarkClick;
    }
};
