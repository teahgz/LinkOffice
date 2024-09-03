let yearCount = 0;

function addYear() {
    yearCount++;

    const container = document.getElementById("vacationFormContainer");

    const inputGroup = document.createElement("div");
    inputGroup.className = "input-group";
    inputGroup.id = `year${yearCount}`;

    const label = document.createElement("label");
    label.textContent = `${yearCount}년차 `;
    inputGroup.appendChild(label);

    const input = document.createElement("input");
    input.type = "number";
    input.name = `vacationData[${yearCount}]`; // Key를 통해 서버로 전달
    input.min = "0";
    inputGroup.appendChild(input);

    container.appendChild(inputGroup);
}

function removeYear() {
    if (yearCount > 0) {
        const container = document.getElementById("vacationFormContainer");
        const lastInputGroup = document.getElementById(`year${yearCount}`);
        if (lastInputGroup) {
            container.removeChild(lastInputGroup);
        }

        yearCount--;
    }
}

