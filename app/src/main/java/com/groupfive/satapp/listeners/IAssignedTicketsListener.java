package com.groupfive.satapp.listeners;

import com.groupfive.satapp.models.tickets.TicketModel;

public interface IAssignedTicketsListener {
    void onAssignedTicketItemClick(TicketModel ticketModel);
}
