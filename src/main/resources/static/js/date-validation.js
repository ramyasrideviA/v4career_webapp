document.addEventListener("DOMContentLoaded", function () {

    const today = new Date().toISOString().split("T")[0];

    const dateInputs = document.querySelectorAll('input[type="date"]');

    dateInputs.forEach(input => {

        if (!input.value) {
            input.value = today;
        }

    });

});