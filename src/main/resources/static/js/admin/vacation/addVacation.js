let yearCount = 0;

function addYear() {
  yearCount++;

  const container = document.getElementById("vacationFormContainer");
  const tableBody = document.getElementById("tableBody");

  const inputGroup = document.createElement("div");
  inputGroup.className = "input-group";
  inputGroup.id = `year${yearCount}`;

  const label = document.createElement("label");
  label.textContent = `${yearCount}년차 `;
  inputGroup.appendChild(label);

  const input = document.createElement("input");
  input.type = "number";
  input.name = `vacationYear${yearCount}`;
  input.min = "0";
  inputGroup.appendChild(input);

  container.appendChild(inputGroup);

  const row = document.createElement("tr");
  row.id = `row${yearCount}`;
  row.innerHTML = `
            <td>${yearCount}년차</td>
            <td>${input.outerHTML}</td>
        `;
  tableBody.appendChild(row);
}

function removeYear() {
  if (yearCount > 0) {

    const container = document.getElementById("vacationFormContainer");
    const lastInputGroup = document.getElementById(`year${yearCount}`);
    if (lastInputGroup) {
      container.removeChild(lastInputGroup);
    }

    const tableBody = document.getElementById("tableBody");
    const lastRow = document.getElementById(`row${yearCount}`);
    if (lastRow) {
      tableBody.removeChild(lastRow);
    }


    yearCount--;
  }
}