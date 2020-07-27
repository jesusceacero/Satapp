package com.groupfive.satapp.models.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarModel {
    long calID;
    String displayName;
    String accountName;
    String ownerName;
}
